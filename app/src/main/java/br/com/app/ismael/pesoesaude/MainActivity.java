package br.com.app.ismael.pesoesaude;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBAdapter db = new DBAdapter(this);
        db.open();
        Intent it = null;
        if (db.getCountUsers() <= 0) {
            it = new Intent(getApplicationContext(), BoasVindasActivity.class);
            startActivity(it);
        } else {
            Cursor c = db.getAllUsers();
            c.moveToFirst();
            String usuario = c.getString(1);
            TextView boasVindas = (TextView) findViewById(R.id.labelBoasVindas);
            boasVindas.setText("Olá, " + usuario + "! Tudo bem?");
            final long idUsuario = c.getLong(0);
            double altura = c.getDouble(4);
            //c = db.getPesoMaisRecente(idUsuario);
            c = db.getDoisUltimosPesos(idUsuario);
            double pesoAtual = c.getDouble(2);
            TextView txtPesoAtual = (TextView) findViewById(R.id.labelPeso);
            txtPesoAtual.setText("Seu peso atual é " + String.format("%.2f", pesoAtual) + " kg.");
            TextView txtIMC = (TextView) findViewById(R.id.labelIMC);
            double imc = pesoAtual / Math.pow(altura / 100, 2.0);
            txtIMC.setText("Seu IMC atual é " + String.format("%.2f", imc) + ".");
            if (c.moveToNext()) {
                double pesoAnterior = c.getDouble(2);
                double diferencaPesos = pesoAnterior - pesoAtual;
                String mensagemVariacaoPeso = "";
                String mensagemMotivadora = "";
                if (diferencaPesos < 0) {
                    mensagemVariacaoPeso = "Você engordou ";
                    diferencaPesos *= -1;
                } else {
                    mensagemVariacaoPeso = "Você emagreceu ";
                }
                mensagemVariacaoPeso += String.format("%.2f", diferencaPesos) + " kg desde a sua última pesagem.";
                TextView txtVariacao = (TextView) findViewById(R.id.labelVariacaoPeso);
                txtVariacao.setText(mensagemVariacaoPeso + "\n" + mensagemMotivadora);
            }
            Button botaoCadastrarPeso = (Button) findViewById(R.id.botaoCadastrarPeso);
            botaoCadastrarPeso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getApplicationContext(), PesoActivity.class);
                    Bundle param = new Bundle();
                    param.putLong("idUsuario", idUsuario);
                    it.putExtras(param);
                    startActivity(it);
                }
            });

        }
    }
}
