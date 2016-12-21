package cpe.top.quizz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import cpe.top.quizz.asyncTask.CreateQuizzTask;
import cpe.top.quizz.asyncTask.GetQuestionsByThemesAndUserTask;
import cpe.top.quizz.asyncTask.responses.AsyncQuestionResponse;
import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;


public class CreateQuizz extends AppCompatActivity implements AsyncQuestionResponse, AsyncUserResponse {

    private static final String STATE = "STATE";
    private static final String THEME = "THEME";
    private static final String USER = "USER";
    private static final String QUIZZNAME = "QUIZZNAME";
    private static final String QUESTIONS = "QUESTIONS";
    private static final String RANDOM = "RANDOM";

    // Max themes by quizz
    final static int MAXTHEMESBYQUIZZ = 2;

    // Themes list took by intent
    ArrayList<Theme> myThemes = new ArrayList<>();
    ArrayList<Question> myQuestions = new ArrayList<>();

    // User took by intent
    private User connectedUser = new User();

    private Boolean questionsTook = false;
    private EditText quizzEditText;
    private RadioButton chooseQuestionButton;
    private RadioButton randomQuestionButton;
    private EditText nbQuestionsEditText;
    private String nbQuestion;
    private String quizzName;
    private Button validate;
    private String state;
    private TextView themesView;


    // TODO : Faire en sorte qu'il ne récupère pas les questions si elles ont deja été récup

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quizz);

        Intent intent = getIntent();
        final TextView textViewTheme = (TextView) findViewById(R.id.textViewTheme);

        if (intent != null) {

            // Take private variables
            myThemes = (ArrayList<Theme>) intent.getSerializableExtra(THEME);

            connectedUser = (User) intent.getSerializableExtra(USER);
            quizzEditText = (EditText) findViewById(R.id.name);
            chooseQuestionButton = (RadioButton) findViewById(R.id.chooseQuest);
            randomQuestionButton = (RadioButton) findViewById(R.id.randomQuest);
            themesView = (TextView) findViewById(R.id.themes);
            nbQuestionsEditText = (EditText) findViewById(R.id.nbQuest);

            if(!"".equals(intent.getStringExtra(QUIZZNAME))) {
                quizzEditText.setText(intent.getStringExtra(QUIZZNAME));
            }

            if(intent.getIntExtra(RANDOM, 0) == 1) {
                chooseQuestionButton.setChecked(true);
            }

            myQuestions = (ArrayList<Question>) intent.getSerializableExtra(QUESTIONS);
            if(myQuestions != null && chooseQuestionButton.isChecked()) {
                nbQuestionsEditText.setText(Integer.toString(myQuestions.size()));
            }

            if(myThemes.size() != 0) {
                String themesChar = "";
                for(Theme t : myThemes) {
                    if("".equals(themesChar)) {
                        themesChar = t.getName();
                    } else {
                        themesChar = themesChar + " - " + t.getName();
                    }
                }
                themesView.setText(themesChar);
            }

            if(myThemes.size() > 1) {
                textViewTheme.setText("Thèmes");
            }
        }

        nbQuestionsEditText.setText(Integer.toString(myQuestions.size()));
        if(randomQuestionButton.isChecked()) {
            nbQuestionsEditText.setText("");
        }

        validate = (Button) findViewById(R.id.validate);

        // Initialize listener
        chooseQuestionButton.setOnClickListener(chooseQListener);
        randomQuestionButton.setOnClickListener(randomQListener);
        validate.setOnClickListener(validateListener);

        // Bouton to add theme
        final Button addTheme = (Button) findViewById(R.id.addTheme);
        addTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myThemes.size() < MAXTHEMESBYQUIZZ) {
                    Intent intent = new Intent(CreateQuizz.this, ChooseTheme.class);
                    state = "Quizz";
                    quizzName = (quizzEditText.getText()).toString();
                    intent.putExtra(USER, connectedUser);
                    intent.putExtra(STATE, state);
                    intent.putExtra(THEME, myThemes);
                    intent.putExtra(QUIZZNAME, quizzName);
                    intent.putExtra(QUESTIONS, myQuestions);
                    intent.putExtra(RANDOM, randomQuestionButton.isChecked() ? 0 : 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateQuizz.this, "Tu ne peux mettre que " + MAXTHEMESBYQUIZZ + " thèmes au maximum", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // LISTENER FOR RANDOM CHOICE QUESTIONS
    private View.OnClickListener randomQListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            nbQuestionsEditText.setText("");
            Toast.makeText(CreateQuizz.this, "Veuillez choisir un nombre de question", Toast.LENGTH_LONG).show();
        }
    };

    // LISTENER TO CHOOSE YOUR QUESTION
    private View.OnClickListener chooseQListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            quizzName = (quizzEditText.getText()).toString();

            //Check param
            if ("".equals(quizzEditText)){
                Toast.makeText(CreateQuizz.this,"Choissisez un nom", Toast.LENGTH_LONG).show();
                randomQuestionButton.setChecked(true);
                return;
            }

            // All check ok
            Intent intent = new Intent(CreateQuizz.this, CreateQuizzChoose.class);
            intent.putExtra(QUIZZNAME, quizzName);
            intent.putExtra(THEME, myThemes);
            intent.putExtra(QUESTIONS, myQuestions);
            intent.putExtra(RANDOM, randomQuestionButton.isChecked() ? 0 : 1);
            intent.putExtra(USER, connectedUser);
            startActivity(intent);
        }
    };

    // LISTENER TO VALID YOUR QUIZZ
    private View.OnClickListener validateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            quizzName = (quizzEditText.getText()).toString();
            nbQuestion = (((EditText) findViewById(R.id.nbQuest)).getText()).toString();

            // Check param
            if ("".equals(quizzName)){
                Toast.makeText(CreateQuizz.this,"Choissisez un nom", Toast.LENGTH_LONG).show();
                return;
            }

            if ("".equals(nbQuestion)){
                Toast.makeText(CreateQuizz.this,"Choissisez un nombre de questions", Toast.LENGTH_LONG).show();
                return;
            }

            GetQuestionsByThemesAndUserTask u = new GetQuestionsByThemesAndUserTask(CreateQuizz.this);
            u.execute(connectedUser, myThemes);
        }
    };
    private Boolean quizzCreated = false;
    private Boolean takeQuestions = false;
    @Override
    public void processFinish(Object obj) {
        if(takeQuestions==false && randomQuestionButton.isChecked()) {
            Collection<Question> questions = (Collection<Question>) ((ReturnObject) obj).getObject();
            int nb = Integer.parseInt(nbQuestion);
            if(questions!=null) {
                Iterator itr = questions.iterator();
                while (itr.hasNext() && nb != 0) {
                    Question q = (Question) itr.next();
                    myQuestions.add(q);
                    nb--;
                }
                takeQuestions = true;
            } else {
                Toast.makeText(CreateQuizz.this,"Aucune question disponible pour ce(s) thème(s)", Toast.LENGTH_LONG).show();
            }
        }


        if(quizzCreated==false && (takeQuestions==true || chooseQuestionButton.isChecked())) {
            if (myQuestions.size() != 0) {
                Quizz myQuizz = new Quizz(quizzName, myQuestions);
                CreateQuizzTask createQuizzTask = new CreateQuizzTask(CreateQuizz.this);
                createQuizzTask.execute(myQuizz);
                quizzCreated = true;

                Toast.makeText(CreateQuizz.this, "Quizz créé", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateQuizz.this, Home.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
            } else {
                Toast.makeText(CreateQuizz.this, "Vous n'avez pas choisi de question", Toast.LENGTH_LONG).show();
            }
        }
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
                Intent intent = new Intent(CreateQuizz.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
