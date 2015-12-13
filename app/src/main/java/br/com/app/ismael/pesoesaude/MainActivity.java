package br.com.app.ismael.pesoesaude;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView noticias = (TextView) findViewById(R.id.lblNoticias);
        noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://g1.globo.com/bemestar/alimentacao/index.html"));
                startActivity(myIntent);
            }
        });
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
            TextView mensagens = (TextView) findViewById(R.id.labelMensagem);
            StringBuilder sbMensagens = new StringBuilder();
            sbMensagens.append("Olá, " + usuario + "! Tudo bem?<p>");
            final long idUsuario = c.getLong(0);
            double altura = c.getDouble(4);
            //c = db.getPesoMaisRecente(idUsuario);
            c = db.getUltimosPesos(idUsuario);
            double pesoAtual = c.getDouble(2);
            sbMensagens.append("Seu peso atual é <b>" + String.format("%.2f", pesoAtual) + "</b> kg ");
            double imc = pesoAtual / Math.pow(altura / 100, 2.0);
            sbMensagens.append("e seu IMC é <b>" + String.format("%.2f", imc) + "</b>.<br>");
            sbMensagens.append("Lembre de se pesar toda semana para controlar seu peso.");
            if (c.moveToNext()) {
                double pesoAnterior = c.getDouble(2);
                StringBuilder mensagemVariacaoPeso = new StringBuilder("Você ");
                double diferencaPesos = verificaDiferencaPeso(pesoAnterior, pesoAtual, mensagemVariacaoPeso);
                mensagemVariacaoPeso.append(String.format("%.2f", diferencaPesos) + " kg desde a sua última pesagem");
                if (c.moveToNext()) {
                    c.moveToLast();
                    mensagemVariacaoPeso.append(" e");
                    double primeiroPeso = c.getDouble(2);
                    double diferencaPesoInicial = verificaDiferencaPeso(primeiroPeso, pesoAtual, mensagemVariacaoPeso);
                    mensagemVariacaoPeso.append(String.format("%.2f", diferencaPesoInicial) + " kg desde o seu primeiro registro");
                }
                mensagemVariacaoPeso.append(".");
                sbMensagens.append("<h2>" + mensagemVariacaoPeso + "</h2>");
            }
            mensagens.setText(Html.fromHtml(sbMensagens.toString()));
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
            Button botaoConsultarPesos = (Button) findViewById(R.id.botaoConsultaPesos);
            botaoConsultarPesos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getApplicationContext(), ListaPesosActivity.class);
                    Bundle param = new Bundle();
                    param.putLong("idUsuario", idUsuario);
                    it.putExtras(param);
                    startActivity(it);
                }
            });

        }
    }

    private double verificaDiferencaPeso(double pesoAnterior, double pesoAtual, StringBuilder mensagemVariacaoPeso) {
        double diferencaPesos = pesoAnterior - pesoAtual;
        if (diferencaPesos < 0) {
            mensagemVariacaoPeso.append(" engordou ");
            diferencaPesos *= -1;
        } else {
            mensagemVariacaoPeso.append(" emagreceu ");
        }
        return diferencaPesos;
    }
}
