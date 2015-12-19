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

/**
 * Activity que representa a tela principal do APP.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * Define a URL para a qual o TextView de notícias irá direcionar.
         */
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
        /*
         * Caso não exista usuário cadastrado, exibe a tela de boas vindas (primeiro uso do app).
         * Senão, exibe alguns dados relativos aos pesos já cadastrados.
         */
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
            /*
             * Recupera os pesos cadastrados ordenados pela data de inserção decrescente.
             */
            c = db.getUltimosPesos(idUsuario);
            /*
             * O peso mais recente é o peso atual.
             */
            double pesoAtual = c.getDouble(2);
            sbMensagens.append("Seu peso atual é <b>" + String.format("%.2f", pesoAtual) + "</b> kg ");
            double imc = pesoAtual / Math.pow(altura / 100, 2.0);
            sbMensagens.append("e seu IMC é <b>" + String.format("%.2f", imc) + "</b>.<br>");
            sbMensagens.append("Lembre de se pesar toda semana para controlar seu peso.");
            /*
             * Utiliza o próximo registro, caso exista, para exibir a variação de peso do usuário.
             */
            if (c.moveToNext()) {
                double pesoAnterior = c.getDouble(2);
                StringBuilder mensagemVariacaoPeso = new StringBuilder("Você ");
                double diferencaPesos = verificaDiferencaPeso(pesoAnterior, pesoAtual, mensagemVariacaoPeso);
                mensagemVariacaoPeso.append(String.format("%.2f", diferencaPesos) + " kg desde a sua última pesagem");
                /*
                 * Caso exista mais algum peso cadastrado, vai para o primeiro registro e mostra
                 * para o usuário a variação de peso desde a primeira utilização do app.
                 */
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
            /*
             * Seta um OnClickListener para direcionar para a tela de cadastro de pesos.
             */
            Button botaoCadastrarPeso = (Button) findViewById(R.id.botaoCadastrarPeso);
            botaoCadastrarPeso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getApplicationContext(), PesoActivity.class);
                    Bundle param = new Bundle();
                    /*
                     * O id do usuário é importante para ser usado como "chave" na tabela de cadastro
                     * de pesos.
                     */
                    param.putLong("idUsuario", idUsuario);
                    it.putExtras(param);
                    startActivity(it);
                }
            });
            Button botaoConsultarPesos = (Button) findViewById(R.id.botaoConsultaPesos);
            /*
             * Seta um OnClickListener para direcionar para a tela de listagem de pesos.
             */
            botaoConsultarPesos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getApplicationContext(), ListaPesosActivity.class);
                    Bundle param = new Bundle();
                    /*
                     * O id do usuário é a "chave" na tabela de cadastro de pesos.
                     */
                    param.putLong("idUsuario", idUsuario);
                    it.putExtras(param);
                    startActivity(it);
                }
            });

        }
    }

    /**
     * Calcula a diferença entre dois pesos e altera o objeto mensagemVariacaoPeso informando se o
     * usuário emagreceu ou engordou.
     *
     * @param pesoAnterior
     * @param pesoAtual
     * @param mensagemVariacaoPeso - Parâmetro que será usado para informar se o usuário engordou ou
     *                             emagreceu.
     * @return a diferença entre o peso anterior e o peso atual. O retorno sempre será positivo.
     */
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
