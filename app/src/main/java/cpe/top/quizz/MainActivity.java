package cpe.top.quizz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cpe.top.quizz.asyncTask.ConnexionTask;
import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.User;

/**
 * @author Louis Paret
 * @version 0.1
 * @since 06/11/2016
 */

public class MainActivity extends AppCompatActivity implements AsyncUserResponse {

    private final static String USER = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView pseudo = (TextView) findViewById(R.id.pseudo);
        final TextView password = (TextView) findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValid()) {
                    ConnexionTask u = new ConnexionTask(MainActivity.this);
                    u.execute(pseudo.getText().toString(), password.getText().toString());
            }
            }
        });

        final TextView createAccount = (TextView) findViewById(R.id.createAccount);

        createAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Inscription.class);
                startActivity(intent);
            }
        });

        final TextView renvoiMdp = (TextView) findViewById(R.id.mdpForgot);

        renvoiMdp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    protected boolean isValid() {

        final TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        final TextView passwordView = (TextView) findViewById(R.id.password);

        final String pseudo = (pseudoView.getText()).toString();
        final String password = (passwordView.getText()).toString();

        // Test login - Not empty
        if ("".equals(pseudo)) {
            Toast.makeText(MainActivity.this, "Le pseudo n'est pas renseigné.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Test password - Not empty
        if ("".equals(password)) {
            Toast.makeText(MainActivity.this, "Le mot de passe n'est pas renseigné.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void processFinish(Object obj) {
        //Object cannot be null
        switch (((ReturnObject) obj).getCode()){
            case ERROR_000:
                User user = (User) ((ReturnObject) obj).getObject();
                if(user.getPseudo() != null || user.getMail() != null){
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra(USER, (User) ((ReturnObject) obj).getObject());
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Erreur interne", Toast.LENGTH_SHORT).show();
                }
                break;
            case ERROR_200:
                Toast.makeText(MainActivity.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_650:
                Toast.makeText(MainActivity.this, "Veuiller activer votre compte", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_100:
            default:
                Toast.makeText(MainActivity.this, "Erreur login/mot de passe", Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
