package cpe.top.quizz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cpe.top.quizz.asyncTask.CreateQuestionTask;
import cpe.top.quizz.asyncTask.CreateResponseTask;
import cpe.top.quizz.asyncTask.FriendsTask;
import cpe.top.quizz.asyncTask.responses.AsyncResponse;
import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Response;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;

/**
 * Created by lparet on 22/11/16.
 */

public class CreateQuestion extends AppCompatActivity implements AsyncResponse, NavigationView.OnNavigationItemSelectedListener {

    final String THEME = "THEME";
    final String USER = "USER";
    final String RESPONSES = "RESPONSES";
    final String EXPLANATION = "EXPLANATION";
    final String QUESTION = "QUESTION";
    private static final String FRIENDS_TASK = "FRIENDS_TASK";
    private static final String QUESTION_TASK = "QUESTION_TASK";
    private static final String LIST_FRIENDS = "LIST_FRIENDS";

    // Max themes by questions
    final static int MAXTHEMESBYQUESTION = 2;

    // Nb responses for a question
    final static int NBRESPONSES = 4;

    // List of responses showed
    public ArrayList responsesList = new ArrayList();

    // Define if one checkbox is checked
    public Boolean oneChecked = false;

    // Themes list took by intent
    ArrayList<Theme> myThemes = new ArrayList<>();

    // User took by intent
    private User connectedUser = null;

    private String explanation, question, pseudo;
    private MyAdapter myAdapter;

    private TextView questionView;
    private TextView explanationView;

    private List<User> listF = null;

    private Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
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

        getWindow().setBackgroundDrawableResource(R.drawable.background);


        final TextView textViewTheme = (TextView) findViewById(R.id.textViewTheme);

        questionView = (TextView) findViewById(R.id.questionLabel);
        explanationView = (TextView) findViewById(R.id.explanationLabel);

        // Take extras in intent (connectedUser, themes, explanation and question... if it was already choosed
        bundle = getIntent().getExtras();
        if (bundle != null) {
            connectedUser = (User) bundle.getSerializable(USER);
            pseudo = connectedUser.getPseudo();

            if(connectedUser == null) {
                Intent i = new Intent(CreateQuestion.this, MainActivity.class);
                startActivity(i);
            }

            // Themes
            myThemes = (ArrayList<Theme>) bundle.getSerializable(THEME);
            if(myThemes.size() > 1) {
                textViewTheme.setText("Thèmes");
            }

            explanation = bundle.getString(EXPLANATION);
            if(!"".equals(explanation)) {
                explanationView.setText(explanation);
            }

            question = bundle.getString(QUESTION);
            if(!"".equals(question)) {
                questionView.setText(question);
            }
        }

        // Bouton to add theme
        final Button addTheme = (Button) findViewById(R.id.addTheme);
        addTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myThemes.size() < MAXTHEMESBYQUESTION) {
                    explanation = (explanationView.getText()).toString();
                    question = (questionView.getText()).toString();

                    Intent intent = new Intent(CreateQuestion.this, ChooseTheme.class);
                    intent.putExtras(bundle);
                    intent.putExtra(EXPLANATION, explanation);
                    intent.putExtra(QUESTION, question);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateQuestion.this, "Tu ne peux mettre que " + MAXTHEMESBYQUESTION + " thèmes au maximum", Toast.LENGTH_LONG).show();
                }
            }
        });

        // TextView to see which themes are choosed
        final TextView themesView = (TextView) findViewById(R.id.themes);
        String themesChar = "";
        for(Theme t : myThemes) {
            if("".equals(themesChar)) {
                themesChar = t.getName();
            } else {
                themesChar = themesChar + " - " + t.getName();
            }
        }
        themesView.setText(themesChar);

        // Initialise listView
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setItemsCanFocus(true);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        // Button to valid question
        final Button validQuestion = (Button) findViewById(R.id.validQuestion);
        validQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isValid(myAdapter)) {
                questionView = (TextView) findViewById(R.id.questionLabel);
                explanationView = (TextView) findViewById(R.id.explanationLabel);

                // Create response in first
                Response reponse1 = new Response(myAdapter.getTextContent(0), myAdapter.isCheckedContent(0));
                Response reponse2 = new Response(myAdapter.getTextContent(1), myAdapter.isCheckedContent(1));
                Response reponse3 = new Response(myAdapter.getTextContent(2), myAdapter.isCheckedContent(2));
                Response reponse4 = new Response(myAdapter.getTextContent(3), myAdapter.isCheckedContent(3));

                // Create List of responses
                ArrayList<Response> myResponses = new ArrayList<>();
                myResponses.add(reponse1);
                myResponses.add(reponse2);
                myResponses.add(reponse3);
                myResponses.add(reponse4);

                // Async Task to add responses in BDD
                CreateResponseTask createResponsesTask = new CreateResponseTask(CreateQuestion.this);
                createResponsesTask.execute(myResponses, pseudo);

                // Create Question after
                Question myQuestion = new Question(question, explanation, pseudo, myThemes);

                // Async Task to add question in BDD
                CreateQuestionTask createQuestionTask = new CreateQuestionTask(CreateQuestion.this);
                createQuestionTask.execute(myQuestion);
            }
            }
        });
    }

    private Boolean isValid(MyAdapter myAdapter) {

        // Test question is not empty
        question = (((TextView) findViewById(R.id.questionLabel)).getText()).toString();
        if ("".equals(question)) {
            Toast.makeText(CreateQuestion.this, "Veuillez rentrer une question", Toast.LENGTH_LONG).show();
            return false;
        }

        // Test question is not empty
        explanation = (((TextView) findViewById(R.id.explanationLabel)).getText()).toString();
        if ("".equals(explanation)) {
            Toast.makeText(CreateQuestion.this, "Veuillez rentrer une explication", Toast.LENGTH_LONG).show();
            return false;
        }

        // Rep 1 & 2
        String rep;
        for (int i = 1; i <= NBRESPONSES; i++) {
            rep = myAdapter.getTextContent(i - 1);
            if ("".equals(rep)) {
                Toast.makeText(CreateQuestion.this, "Réponse " + i + " non renseignée", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        // checked
        Boolean checkBox1 = myAdapter.isCheckedContent(0);
        Boolean checkBox2 = myAdapter.isCheckedContent(1);
        Boolean checkBox3 = myAdapter.isCheckedContent(2);
        Boolean checkBox4 = myAdapter.isCheckedContent(3);

        // test if one of checkbox is choosed
        if (!(checkBox1 || checkBox2 || checkBox3 || checkBox4)) {
            Toast.makeText(CreateQuestion.this, "Vous n'avez pas renseigné de bonne réponse", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void processFinish(Object obj) {

        try {
            if (((List<Object>) obj).get(0) != null && ((ReturnObject) ((List<Object>) obj).get(0)).getObject().equals(QUESTION_TASK)) {
                // Case of QuizzTask
                processFinishQuestionTask(obj);
            } else if (((List<Object>) obj).get(0) != null && ((ReturnObject) ((List<Object>) obj).get(0)).getObject().equals(FRIENDS_TASK)) {
                // Case of FriendsTask
                processFinishFriendsTask(obj);
            }
        } catch (ClassCastException e) {
            processFinishExceptionCast(obj);
        }
    }

    private void processFinishFriendsTask(Object obj) {
        switch (((ReturnObject) ((List<Object>) obj).get(1)).getCode()) {
            case ERROR_000:
                Intent myIntent = new Intent(CreateQuestion.this, FriendsDisplay.class);
                this.listF = (List<User>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
                myIntent.putExtra(USER, (User) connectedUser);
                myIntent.putExtra(LIST_FRIENDS, (ArrayList<User>) listF);
                startActivity(myIntent);
                break;
            case ERROR_200:
                Toast.makeText(CreateQuestion.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            // Temporarily - When no data found - ERROR_50 is ok?
            case ERROR_050:
                // No friends for the user but we want to access to FriendsDisplay
                Intent intentFriends = new Intent(CreateQuestion.this, FriendsDisplay.class);
                this.listF = (List<User>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
                intentFriends.putExtra(USER, (User) connectedUser);
                intentFriends.putExtra(LIST_FRIENDS, (ArrayList<User>) listF);
                startActivity(intentFriends);
                break;
            case ERROR_100:
                // No friends for the user but we want to access to FriendsDisplay
                Intent intentFriends_100 = new Intent(CreateQuestion.this, FriendsDisplay.class);
                this.listF = (List<User>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
                intentFriends_100.putExtra(USER, (User) connectedUser);
                intentFriends_100.putExtra(LIST_FRIENDS, (ArrayList<User>) listF);
                startActivity(intentFriends_100);
                break;
            default:
                Toast.makeText(CreateQuestion.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void processFinishExceptionCast(Object obj) {
        switch (((ReturnObject) obj).getCode()) {
            case ERROR_200:
                Toast.makeText(CreateQuestion.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_100:
            default:
                Toast.makeText(CreateQuestion.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void processFinishQuestionTask(Object obj) {
        switch (((ReturnObject) ((List<Object>) obj).get(1)).getCode()){
            case ERROR_000:
                Intent intent = new Intent(CreateQuestion.this, Home.class);
                intent.putExtras(bundle);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                Toast.makeText(CreateQuestion.this, "Question créée !", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case ERROR_200:
                Toast.makeText(CreateQuestion.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(CreateQuestion.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    class ViewHolder {
        CheckBox checkBox;
        EditText caption;
    }

    class ListItem {
        Boolean checked;
        String caption;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 1; i <= NBRESPONSES; i++) {
                ListItem listItem = new ListItem();
                listItem.checked = false;
                responsesList.add(listItem);
            }
            notifyDataSetChanged();
        }

        public int getCount() {
            return responsesList.size();
        }

        // used to get the text into an EditText
        public String getTextContent(int position) {
            final LinearLayout en = (LinearLayout) myAdapter.getView(position, null, null);
            final EditText ed = (EditText) en.getChildAt(1);
            String rep = (ed.getText()).toString();
            return rep;
        }

        // used to get the value of a CheckBox
        public Boolean isCheckedContent(int position) {
            final LinearLayout en = (LinearLayout) myAdapter.getView(position, null, null);
            final CheckBox ed = (CheckBox) en.getChildAt(0);
            Boolean checked = ed.isChecked();
            return checked;
        }

        public Object getItem(int position) {
            return getView(position, null, null);
        }

        public long getItemId(int position) {
            return getView(position, null, null).getId();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listview_reponses, null);
                holder.caption = (EditText) convertView.findViewById(R.id.editText);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Fill EditText with the value you have in data source
            holder.caption.setText(((ListItem) responsesList.get(position)).caption);
            holder.caption.setId(position);

            holder.checkBox.setChecked(((ListItem) responsesList.get(position)).checked);
            holder.checkBox.setId(position);

            // we need to update adapter once we finish with editing
            holder.caption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        ((ListItem) responsesList.get(position)).caption = Caption.getText().toString();
                    }
                }
            });

            // we need to update adapter once we check a box
            // impossible to check a second CheckBox
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = v.getId();
                    final CheckBox Caption = (CheckBox) v;
                    if (oneChecked && Caption.isChecked()) {
                        ((ListItem) responsesList.get(position)).checked = false;
                        notifyDataSetChanged();
                        Toast.makeText(CreateQuestion.this, "Vous ne pouvez cocher qu'une seule réponse", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (Caption.isChecked()) {
                        ((ListItem) responsesList.get(position)).checked = Caption.isChecked();
                        oneChecked = true;
                    } else {
                        ((ListItem) responsesList.get(position)).checked = false;
                        oneChecked = false;
                    }
                }
            });
            return convertView;
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
        return false;
    }

    public void onBackPressed(){
        Intent intent = new Intent(CreateQuestion.this, Home.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(CreateQuestion.this, Home.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.friends:
                FriendsTask friends = new FriendsTask(CreateQuestion.this);
                friends.execute(connectedUser.getPseudo());
                break;
            case R.id.findFriend:
                intent = new Intent(CreateQuestion.this, ChooseFriends.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.chat:
                intent = new Intent(CreateQuestion.this, Chat.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.findQuiz:
                intent = new Intent(CreateQuestion.this, FindQuizz.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.evalMode:
                intent = new Intent(CreateQuestion.this, EvalMode.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.createEvaluation:
                intent = new Intent(CreateQuestion.this, ChooseQuizzEval.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.logout:
                // Destroy user and return to main activity
                connectedUser = null;
                Toast.makeText(this, "A bientôt !", Toast.LENGTH_LONG).show();
                intent = new Intent(CreateQuestion.this, MainActivity.class);
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