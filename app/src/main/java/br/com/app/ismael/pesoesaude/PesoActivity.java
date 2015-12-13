package br.com.app.ismael.pesoesaude;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class PesoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peso);
        final long idUsuario = getIntent().getExtras().getLong("idUsuario");
        Button botaoEnviar = (Button) findViewById(R.id.botaoEnviar);
        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double pesoCadastrado = Double.parseDouble(((TextView) findViewById(R.id.txtPeso)).getText().toString());
                String dataCadastrada = ((TextView) findViewById(R.id.txtCadastroPesoData)).getText().toString();
                DBAdapter db = new DBAdapter(v.getContext());
                db.open();
                long retornoInsertPeso = 0;
                try {
                    retornoInsertPeso = db.insertPeso(idUsuario, pesoCadastrado, new SimpleDateFormat("dd/MM/yyyy").parse(dataCadastrada).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String mensagemRetorno = "";
                if (retornoInsertPeso > 0) {
                    mensagemRetorno = "Peso cadastrado com sucesso.";
                } else {
                    mensagemRetorno = "Ocorreu um erro ao tentar cadastrar esse peso.";
                }
                Toast.makeText(PesoActivity.this, mensagemRetorno, Toast.LENGTH_LONG).show();
                Intent it = new Intent(v.getContext(), MainActivity.class);
                startActivity(it);
            }
        });
    }
}
