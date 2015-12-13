package br.com.app.ismael.pesoesaude;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ListaPesosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pesos);
        final long idUsuario = getIntent().getExtras().getLong("idUsuario");
        ListView listViewPesos, listViewPesosTitulos;
        ArrayList<HashMap<String, String>> mylist, mylist_title;
        ListAdapter adapter_title, adapter;
        HashMap<String, String> map1, map2;
        mylist = new ArrayList<HashMap<String, String>>();
        mylist_title = new ArrayList<HashMap<String, String>>();


        map1 = new HashMap<String, String>();

        map1.put("data", "Data");
        map1.put("peso", "Peso");
        mylist_title.add(map1);


        try {
            adapter_title = new SimpleAdapter(this, mylist_title, R.layout.row,
                    new String[]{"data", "peso"}, new int[]{
                    R.id.data, R.id.peso});
            listViewPesosTitulos = (ListView) findViewById(R.id.listViewPesosTitulos);
            listViewPesosTitulos.setAdapter(adapter_title);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /********************************************************/
        /**********Display the contents************/
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c = db.getAllPesosUsuario(idUsuario);
        c.moveToFirst();
        do {
            map2 = new HashMap<String, String>();
            map2.put("data", new SimpleDateFormat("dd/MM/yyyy").format(new Date(c.getLong(3))));
            map2.put("peso", Double.toString(c.getDouble(2)));
            mylist.add(map2);
        } while (c.moveToNext());


        try {
            adapter = new SimpleAdapter(this, mylist, R.layout.row,
                    new String[]{"data", "peso"}, new int[]{
                    R.id.data, R.id.peso});
            listViewPesos = (ListView) findViewById(R.id.listViewPesos);
            listViewPesos.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /********************************************************/

    }


}
