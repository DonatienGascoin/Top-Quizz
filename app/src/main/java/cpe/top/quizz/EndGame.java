package cpe.top.quizz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndGame extends AppCompatActivity {

    private static final String GOODQUESTIONS = "GOODQUESTIONS";
    private static final String BADQUESTIONS = "BADQUESTIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Button closeEnd = (Button) findViewById(R.id.CloseEnd);

        closeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Score (IHM)
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        Intent thisIntent = getIntent();
        Bundle extras = thisIntent.getExtras();
        int goodQuestions = extras.getInt(GOODQUESTIONS);
        int badQuestions = extras.getInt(BADQUESTIONS);
        scoreView.setText("Fin du Quizz. Score de " + goodQuestions + "/" + (goodQuestions + badQuestions));
    }

}
