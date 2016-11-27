package cpe.top.quizz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.mail.MessagingException;

import cpe.top.quizz.asyncTask.AddUserTask;
import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.User;
import cpe.top.quizz.utils.Mail;

/**
 * @author Louis Paret
 * @since 06/11/2016
 * @version 0.1
 */

public class Inscription extends AppCompatActivity implements AsyncUserResponse {

    private final static String USER = "USER";
    final static String EMAIL = "Email";
    final static int MINIMALSIZEPASSWORD = 4;
    final static int MINIMALSIZELOGIN = 3;


    // Contents
    String pseudo;
    String password;
    String confirmPassword;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        // Email text
        final Button validButton = (Button) findViewById(R.id.validate);

        // Widgets
        final TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        final TextView passwordView = (TextView) findViewById(R.id.password);
        final TextView confirmPasswordView = (TextView) findViewById(R.id.confirmPassword);
        final TextView emailView = (TextView) findViewById(R.id.email);


        validButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pseudo = (pseudoView.getText()).toString();
                password = (passwordView.getText()).toString();
                confirmPassword = (confirmPasswordView.getText()).toString();
                email = (emailView.getText()).toString();

                if (isValid()) {
                    AddUserTask u = new AddUserTask(Inscription.this);
                    u.execute(pseudo, email, password);
                    sendEmailAsync sendEmail = new sendEmailAsync();
                    sendEmail.execute();
                }

            }
        });
    }

    private class sendEmailAsync extends AsyncTask<Void, Void, Void> {

        final String email = (((TextView) findViewById(R.id.email)).getText()).toString();
        final String pseudo = (((TextView) findViewById(R.id.pseudo)).getText()).toString();

        String lien="http://163.172.91.2:8090/user/activeUser?mail=" + email;

        final String subject = "Top Quizz - Inscription";
        final String body = "Bonjour " + pseudo + ",\n\nBienvenue sur Top Quizz\nVeuillez ouvrir ce lien pour valider votre inscription :\n " + lien + "\n\nA bientôt sur Top Quizz";

        protected Void doInBackground(Void... arg0) {
            try {
                // Send email
                Mail.sendEmail(email, subject, body);
            } catch (MessagingException em) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        Toast.makeText(Inscription.this, "Impossible d'envoyer l'email.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

    // Test method
    public boolean isValid() {

        // Test login - Not empty and short
        if (pseudo.length() < MINIMALSIZELOGIN) {
            if ("".equals(pseudo)) {
                Toast.makeText(Inscription.this, "Le pseudo n'est pas renseigné.", Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(Inscription.this, "Le pseudo doit contenir au moins " + MINIMALSIZELOGIN + " caractères.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Test password - Not empty and short
        if (password.length() < MINIMALSIZEPASSWORD) {
            if ("".equals(password)) {
                Toast.makeText(Inscription.this, "Le mot de passe n'est pas renseigné.", Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(Inscription.this, "Le mot de passe doit contenir au moins " + MINIMALSIZEPASSWORD + " caractères.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Test equals password and confirm
        if (!(password.equals(confirmPassword))) {
            Toast.makeText(Inscription.this, "Le mot de passe et sa confirmation ne sont pas similaires.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Test confirm password - Not empty and short
        if (confirmPassword.length() < MINIMALSIZEPASSWORD) {
            if ("".equals(confirmPassword)) {
                Toast.makeText(Inscription.this, "Le champ confirmation mot de passe n'est pas renseigné.", Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(Inscription.this, "Le champ confirmation mot de passe doit contenir au moins " + MINIMALSIZEPASSWORD + " caractères.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Test email - Not empty and good email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if ("".equals(email)) {
                Toast.makeText(Inscription.this, "L'email n'est pas renseigné.", Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(Inscription.this, "L'email n'est pas valide.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void processFinish(Object obj) {
        switch (((ReturnObject) obj).getCode()) {
            case ERROR_000:
                Intent intent = new Intent(Inscription.this, InscriptionConfirm.class);
                intent.putExtra(USER, (User) ((ReturnObject) obj).getObject());
                intent.putExtra(EMAIL, email);
                startActivity(intent);
                break;
            case ERROR_300:
                Toast.makeText(Inscription.this, "Pseudo déjà éxistant", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_350:
                Toast.makeText(Inscription.this, "Email déjà éxistant", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_200:
            default:
                Toast.makeText(Inscription.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}