package cpe.top.quizz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cpe.top.quizz.asyncTask.responses.AsyncQuizzResponse;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.User;

public class Home extends AppCompatActivity implements AsyncQuizzResponse {

    private static final String USER = "USER";
    private static final String QUIZZ = "QUIZZ";
    private static final String LIST_QUIZZ = "LIST_QUIZZ";
    private static final String STATE = "STATE";

    private User connectedUser;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        List<Quizz> listQ = null;

        Intent intent = getIntent();
        connectedUser = (User) getIntent().getSerializableExtra(USER);
        if (intent != null && getIntent().getSerializableExtra(LIST_QUIZZ) != null) {

            // User's list of Quizz
            listQ = (List<Quizz>) getIntent().getSerializableExtra(LIST_QUIZZ);

            // Adapter
            QuizzAdapter adapter = new QuizzAdapter(this, listQ, connectedUser);

            // The list (IHM)
            ListView list = (ListView) findViewById(R.id.listQuizz);

        //Initialisation de la liste avec les données
            list.setAdapter(adapter);

            Toast.makeText(Home.this, "Salut " + connectedUser.getPseudo() + " !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Home.this, "Aucun quiz de créé sur ce compte !", Toast.LENGTH_SHORT).show();
        }

        final Button theme = (Button) findViewById(R.id.theme);

        final Button questionButton = (Button) findViewById(R.id.questionButton);

        theme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ThemesDisplay.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
            }

        });
        
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ChooseTheme.class);
                state = "Question";
                intent.putExtra(STATE, state);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
            }
        });

        final Button createQuizz = (Button) findViewById(R.id.addQuizz);
        createQuizz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ChooseTheme.class);
                state = "Quizz";
                intent.putExtra(STATE, state);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
                break;
            case R.id.logout:
                // Destroy user and return to main activity
                connectedUser = null;
                Toast.makeText(this, "A bientôt !", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void processFinish(Object obj) {
        switch (((ReturnObject) obj).getCode()){
            case ERROR_000:
                Intent myIntent = new Intent(Home.this, StartQuizz.class);
                myIntent.putExtra(QUIZZ, (Quizz) ((ReturnObject) obj).getObject());
                startActivity(myIntent);
                finish();
                break;
            case ERROR_200:
                Toast.makeText(Home.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_100:
            default:
                Toast.makeText(Home.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
