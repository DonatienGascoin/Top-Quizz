package cpe.top.quizz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 *
 * @author Louis Paret
 * @since 06/11/2016
 * @version 0.1
 */

public class ResetPasswordConfirm extends AppCompatActivity {
    final String EMAIL = "Email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_confirm);

        Intent intent = getIntent();
        final TextView email = (TextView) findViewById(R.id.email);

        if (intent != null) {
            email.setText(intent.getStringExtra(EMAIL));
        }

        final Button validButton = (Button) findViewById(R.id.validate);

        validButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordConfirm.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
