package cpe.top.quizz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cpe.top.quizz.asyncTask.ChangePasswordTask;
import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.User;
/**
 *
 * @author Louis Paret
 * @since 06/11/2016
 * @version 0.1
 */

public class ResetPassword extends AppCompatActivity implements AsyncUserResponse {
    final String EMAIL = "Email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        final Button validButton = (Button) findViewById(R.id.validate);
        final TextView emailView = (TextView) findViewById(R.id.email);

        validButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String email = (emailView.getText()).toString();

                if(isValid()) {
                    ChangePasswordTask u = new ChangePasswordTask(ResetPassword.this);
                    u.execute(email);
                }
            }
        });
    }

    protected boolean isValid() {

        final TextView emailView = (TextView) findViewById(R.id.email);
        final String email = (emailView.getText()).toString();

        // Test email - Not empty and good email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if("".equals(email)) {
                Toast.makeText(ResetPassword.this, "L'email n'est pas renseigné.", Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(ResetPassword.this, "L'email n'est pas valide.", Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(ResetPassword.this, ResetPasswordConfirm.class);
                    intent.putExtra(EMAIL, user.getMail());
                    startActivity(intent);
                } else {
                    Toast.makeText(ResetPassword.this, "Erreur interne", Toast.LENGTH_SHORT).show();
                }
                break;
            case ERROR_200:
                Toast.makeText(ResetPassword.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_100:
                Toast.makeText(ResetPassword.this, "L'utilisateur n'existe pas", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_050:
                Toast.makeText(ResetPassword.this, "L'email n'a pas pu s'envoyer", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
