package br.com.app.ismael.pesoesaude;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IDADE = "idade";
    private static final String KEY_ALTURA = "altura";
    private static final String KEY_PESO = "peso";
    private static final String KEY_ID_USER = "_idUser";
    private static final String KEY_DATA = "data";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "PesoESaude";
    private static final String DATABASE_TABLE_USER = "users";
    private static final String DATABASE_TABLE_PESO = "pesos";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_TABLE_USERS =
            "create table " + DATABASE_TABLE_USER + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_NAME + " text not null, " + KEY_EMAIL + " text not null, " + KEY_IDADE +
                    " integer not null, " + KEY_ALTURA + " integer not null);";
    private static final String DATABASE_CREATE_TABLE_PESOS =
            "create table " + DATABASE_TABLE_PESO + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_ID_USER + " integer not null, " + KEY_DATA + " integer not null, " + KEY_PESO + " real not null);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertUser(String name, String email, int idade, int altura) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_IDADE, idade);
        initialValues.put(KEY_ALTURA, altura);
        return db.insert(DATABASE_TABLE_USER, null, initialValues);
    }

    public long insertPeso(long idUser, double peso, long data) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID_USER, idUser);
        initialValues.put(KEY_PESO, peso);
        initialValues.put(KEY_DATA, data);
        return db.insert(DATABASE_TABLE_PESO, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteUser(long rowId) {
        return db.delete(DATABASE_TABLE_USER, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllUsers() {
        return db.query(DATABASE_TABLE_USER, new String[]{KEY_ROWID, KEY_NAME,
                KEY_EMAIL, KEY_IDADE, KEY_ALTURA}, null, null, null, null, null);
    }

    public Cursor getAllPesosUsuario(long idUsuario) {
        return db.query(DATABASE_TABLE_PESO, new String[]{KEY_ROWID, KEY_ID_USER, KEY_PESO,
                KEY_DATA}, KEY_ID_USER + " = " + idUsuario, null, null, null, null);
    }

    public Cursor getPesoMaisRecente(long idUser) {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_PESO, new String[]{KEY_ROWID, KEY_ID_USER, KEY_PESO,
                                KEY_DATA}, KEY_ID_USER + "=" + idUser, null,
                        null, null, KEY_DATA, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getUltimosPesos(long idUser) {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_PESO, new String[]{KEY_ROWID, KEY_ID_USER, KEY_PESO,
                                KEY_DATA}, KEY_ID_USER + "=" + idUser, null,
                        null, null, KEY_DATA + " desc", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves a particular contact---
    public Cursor getUser(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_USER, new String[]{KEY_ROWID,
                                KEY_NAME, KEY_EMAIL}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateUser(long rowId, String name, String email, int idade, int altura) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_EMAIL, email);
        args.put(KEY_IDADE, idade);
        args.put(KEY_ALTURA, altura);
        return db.update(DATABASE_TABLE_USER, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public long getCountUsers() {
        return DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_USER);
    }

    public long getCountPesos() {
        return DatabaseUtils.queryNumEntries(db, DATABASE_TABLE_PESO);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_TABLE_USERS);
                db.execSQL(DATABASE_CREATE_TABLE_PESOS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }
}
