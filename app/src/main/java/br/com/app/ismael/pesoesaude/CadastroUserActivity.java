package br.com.app.ismael.pesoesaude;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_user);
        Button btnCadastro = (Button) findViewById(R.id.botaoEnviar);
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = ((EditText) findViewById(R.id.txtNomeCompleto)).getText().toString();
                String email = ((EditText) findViewById(R.id.txtEmail)).getText().toString();
                int idade = Integer.parseInt(((EditText) findViewById(R.id.txtIdade)).getText().toString());
                double peso = Double.parseDouble(((EditText) findViewById(R.id.txtPeso)).getText().toString());
                int altura = Integer.parseInt(((EditText) findViewById(R.id.txtAltura)).getText().toString());
                String dataPesagem = ((EditText) findViewById(R.id.txtDataPesoCadastro)).getText().toString();
                DBAdapter db = new DBAdapter(v.getContext());
                db.open();
                long retornoUser = 0;
                long retornoPeso = 0;
                retornoUser = db.insertUser(usuario, email, idade, altura);
                if (retornoUser > 0) {
                    try {
                        retornoPeso = db.insertPeso(retornoUser, peso, new SimpleDateFormat("dd/MM/yyyy").parse(dataPesagem).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String mensagemRetorno = "";
                if (retornoPeso > 0) {
                    mensagemRetorno = "Usuário cadastrado com sucesso.";
                } else {
                    mensagemRetorno = "Ocorreu um erro ao gravar o usuário.";
                }
                Toast.makeText(CadastroUserActivity.this, mensagemRetorno, Toast.LENGTH_LONG).show();
                Intent it = new Intent(v.getContext(), MainActivity.class);
                startActivity(it);
            }
        });
    }
}
