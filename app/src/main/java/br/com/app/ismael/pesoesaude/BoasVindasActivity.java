package br.com.app.ismael.pesoesaude;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Classe que representa a activity de Boas Vindas do APK.
 */
public class BoasVindasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);
        /*
         * Texto de boas vindas.
         */
        String textoBoasVindas = "Bem vindo ao aplicativo Peso & Saúde. Com ele você pode cadastrar " +
                "e monitorar seu peso e IMC. Toque no botão abaixo para efetuar seu cadastro.";
        ((TextView) findViewById(R.id.lblBoasVindas)).setText(textoBoasVindas);
        /*
         * A funcionalidade do botão é iniciar a activity de cadastro de usuário.
         */
        ((Button) findViewById(R.id.botaoCadastrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), CadastroUserActivity.class);
                startActivity(it);
            }
        });
    }
}
