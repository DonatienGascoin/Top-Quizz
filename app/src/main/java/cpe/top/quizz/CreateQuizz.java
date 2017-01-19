package cpe.top.quizz;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import java.util.Iterator;

import cpe.top.quizz.asyncTask.CreateQuizzTask;
import cpe.top.quizz.asyncTask.FriendsTask;
import cpe.top.quizz.asyncTask.GetQuestionsByThemesAndUserTask;
import cpe.top.quizz.asyncTask.responses.AsyncResponse;
import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;


public class CreateQuizz extends AppCompatActivity implements AsyncResponse, NavigationView.OnNavigationItemSelectedListener {

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

    // Use in processFinish()
    private Boolean quizzCreated = false;
    private Boolean takeQuestions = false;

    // User took by intent
    private User connectedUser = new User();

    private EditText quizzEditText;
    private RadioButton chooseQuestionButton;
    private RadioButton randomQuestionButton;
    private EditText nbQuestionsEditText;
    private String nbQuestion;
    private String quizzName;
    private Button validate;
    private TextView themesView;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quizz);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Take connectedUser, themes and quizzName and questions if it was laready choosed
        bundle = getIntent().getExtras();
        final TextView textViewTheme = (TextView) findViewById(R.id.textViewTheme);

        if (bundle != null) {

            // Take private variables
            myThemes = (ArrayList<Theme>) bundle.getSerializable(THEME);
            connectedUser = (User) bundle.getSerializable(USER);

            if(connectedUser == null) {
                Intent i = new Intent(CreateQuizz.this, MainActivity.class);
                startActivity(i);
            }

            quizzEditText = (EditText) findViewById(R.id.name);
            chooseQuestionButton = (RadioButton) findViewById(R.id.chooseQuest);
            randomQuestionButton = (RadioButton) findViewById(R.id.randomQuest);
            themesView = (TextView) findViewById(R.id.themes);
            nbQuestionsEditText = (EditText) findViewById(R.id.nbQuest);

            if(!"".equals(bundle.getString(QUIZZNAME))) {
                quizzEditText.setText(bundle.getString(QUIZZNAME));
            }

            if(bundle.getInt(RANDOM) == 1) {
                chooseQuestionButton.setChecked(true);
            }

            if(bundle.getSerializable(QUESTIONS) != null) {
                myQuestions = (ArrayList<Question>) bundle.getSerializable(QUESTIONS);
                if(chooseQuestionButton.isChecked()) {
                    nbQuestionsEditText.setText(Integer.toString(myQuestions.size()));
                }
            }

            if(myThemes != null) {
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
                    intent.putExtras(bundle);
                    // put extras to overwrite previous datas
                    quizzName = (quizzEditText.getText()).toString();
                    intent.putExtra(QUIZZNAME, quizzName);
                    intent.putExtra(QUESTIONS, myQuestions);
                    intent.putExtra(RANDOM, randomQuestionButton.isChecked() ? 0 : 1);
                    startActivity(intent);
                    finish();
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
            return;
        }
    };

    // LISTENER TO CHOOSE YOUR QUESTION
    private View.OnClickListener chooseQListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // All check ok
            Intent intent = new Intent(CreateQuizz.this, CreateQuizzChoose.class);
            intent.putExtras(bundle);
            // to overwrite previous datas
            quizzName = (quizzEditText.getText()).toString();
            intent.putExtra(QUIZZNAME, quizzName);
            intent.putExtra(QUESTIONS, myQuestions);
            intent.putExtra(RANDOM, randomQuestionButton.isChecked() ? 0 : 1);
            startActivity(intent);
            finish();
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

    @Override
    public void processFinish(Object obj) {
        if(takeQuestions==false && randomQuestionButton.isChecked()) {
            Collection<Question> questions = (Collection<Question>) ((ReturnObject) obj).getObject();
            int nb = Integer.parseInt(nbQuestion);
            if(questions!=null) {
                if(questions.size() < nb) {
                    Toast.makeText(CreateQuizz.this,"Pas assez de questions disponibles pour ce(s) thèmes", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Iterator itr = questions.iterator();
                    while (itr.hasNext() && nb != 0) {
                        Question q = (Question) itr.next();
                        myQuestions.add(q);
                        nb--;
                    }
                    takeQuestions = true;
                }
            } else {
                Toast.makeText(CreateQuizz.this,"Aucune question disponible pour ce(s) thème(s)", Toast.LENGTH_LONG).show();
                return;
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
                // pass connectedUser and quizzs
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CreateQuizz.this, "Vous n'avez pas choisi de question", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onBackPressed(){
        Intent intent = new Intent(CreateQuizz.this, Home.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.friends:
                FriendsTask friends = new FriendsTask(CreateQuizz.this);
                friends.execute(connectedUser.getPseudo());
                break;
            case R.id.findFriend:
                intent = new Intent(CreateQuizz.this, ChooseFriends.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.chat:
                intent = new Intent(CreateQuizz.this, Chat.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.findQuiz:
                intent = new Intent(CreateQuizz.this, FindQuizz.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.createEvaluation:
                intent = new Intent(CreateQuizz.this, ChooseQuizzEval.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.logout:
                // Destroy user and return to main activity
                connectedUser = null;
                Toast.makeText(this, "A bientôt !", Toast.LENGTH_LONG).show();
                intent = new Intent(CreateQuizz.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            /*case R.id.settings:
                //TODO
                Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
                break;*/
            default:
                //Unreachable statement
                break;
        }
        return true;
    }
}
