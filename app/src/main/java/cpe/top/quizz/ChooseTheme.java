package cpe.top.quizz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cpe.top.quizz.asyncTask.FriendsTask;
import cpe.top.quizz.asyncTask.GetAllThemesTask;
import cpe.top.quizz.asyncTask.responses.AsyncResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;
import cpe.top.quizz.utils.ListViewAdapterThemes;

/**
 * Created by lparet on 29/11/16.
 */

public class ChooseTheme extends AppCompatActivity implements SearchView.OnQueryTextListener, AsyncResponse, NavigationView.OnNavigationItemSelectedListener {
    private static final String THEME = "THEME";
    private static final String STATE = "STATE";
    private static final String USER = "USER";
    private static final String FRIENDS_TASK = "FRIENDS_TASK";
    private static final String THEME_TASK = "THEME_TASK";
    private static final String LIST_FRIENDS = "LIST_FRIENDS";
    private static final String CREATE_QUESTION = "CREATE_QUESTION";

    private Bundle bundle;
    private String state;
    private User connectedUser = null;
    private String createQuestion = null;

    ListViewAdapterThemes adapter;
    SearchView editsearch;
    private ListView list;
    private List<User> listF = null;

    // List of themes - to add multiple themes
    private ArrayList<Theme> myThemes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_theme);
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

        Intent intent = getIntent();
        // take connectedUser and state
        // If you was in CreateQuestion or Create Quizz before : you have themes, question, explanation, quizz name, number of questions choosed
        bundle = intent.getExtras();
        if(bundle != null) {
            myThemes = (ArrayList<Theme>) bundle.getSerializable(THEME);
            state = bundle.getString(STATE);
            connectedUser = (User) bundle.getSerializable(USER);
            createQuestion = (String) bundle.getSerializable(CREATE_QUESTION);

            if (createQuestion == null) {
                LinearLayout toHiddenActivity = (LinearLayout) findViewById(R.id.toHiddenActivity);
                toHiddenActivity.setVisibility(View.INVISIBLE);
            }
        }

        if(connectedUser==null) {
            Intent i = new Intent(ChooseTheme.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        // change the title
        TextView title = (TextView) findViewById(R.id.title);
        if("Quizz".equals(state)) {
            title.setText("Choisis le thème de ton quizz");
        }

        // AsyncTask to take all Themes
        GetAllThemesTask getAllThemesTask = new GetAllThemesTask(ChooseTheme.this);
        getAllThemesTask.execute();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

    @Override
    public void processFinish(Object obj) {

        try {
            if (((List<Object>) obj).get(0) != null && ((ReturnObject) ((List<Object>) obj).get(0)).getObject().equals(THEME_TASK)) {
                // Case of QuizzTask
                processFinishThemeTask(obj);
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
                Intent myIntent = new Intent(ChooseTheme.this, FriendsDisplay.class);
                this.listF = (List<User>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
                myIntent.putExtra(USER, (User) connectedUser);
                myIntent.putExtra(LIST_FRIENDS, (ArrayList<User>) listF);
                startActivity(myIntent);
                break;
            case ERROR_200:
                Toast.makeText(ChooseTheme.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            // Temporarily - When no data found - ERROR_50 is ok?
            case ERROR_050:
                // No friends for the user but we want to access to FriendsDisplay
                Intent intentFriends = new Intent(ChooseTheme.this, FriendsDisplay.class);
                this.listF = (List<User>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
                intentFriends.putExtra(USER, (User) connectedUser);
                intentFriends.putExtra(LIST_FRIENDS, (ArrayList<User>) listF);
                startActivity(intentFriends);
                break;
            case ERROR_100:
                // No friends for the user but we want to access to FriendsDisplay
                Intent intentFriends_100 = new Intent(ChooseTheme.this, FriendsDisplay.class);
                this.listF = (List<User>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
                intentFriends_100.putExtra(USER, (User) connectedUser);
                intentFriends_100.putExtra(LIST_FRIENDS, (ArrayList<User>) listF);
                startActivity(intentFriends_100);
                break;
            default:
                Toast.makeText(ChooseTheme.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void processFinishExceptionCast(Object obj) {

        switch (((ReturnObject) obj).getCode()) {
            case ERROR_200:
                Toast.makeText(ChooseTheme.this, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                break;
            case ERROR_100:
            default:
                Toast.makeText(ChooseTheme.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void processFinishThemeTask(Object obj) {
        Collection<Theme> themes = null ;
        if (obj != null && ((ReturnObject) ((List<Object>) obj).get(1)).getObject() != null){
            themes = (Collection<Theme>) ((ReturnObject) ((List<Object>) obj).get(1)).getObject();
        }
        ArrayList<Theme> resultsList = new ArrayList<>();

        // This algo is to delete theme already choose in the list of theme, like that, you can't choose a theme you have already choose
        if(themes != null && themes.size() != 0) {
            for (Theme t : themes) {
                if (myThemes != null) {
                    for(Theme theme : myThemes) {
                        if (!t.getName().equals(theme.getName())) {
                            resultsList.add(t);
                        }
                    }
                } else {
                    resultsList.add(t);
                }
            }
        }

        Set set = new HashSet() ;
        set.addAll(resultsList) ;
        resultsList = new ArrayList(set);


        // Take connected user to send to ListViewAdapter class
        // Take all things in bundle and add in new intent to transfer to ListViewAdapterThemes.class
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(ChooseTheme.this, ListViewAdapterThemes.class);
        intent.putExtras(bundle);
        intent.putExtra(THEME, myThemes);

        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapterThemes(this, resultsList, intent);

        list = (ListView) findViewById(R.id.listViewTheme);
        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in activity_choose_theme
        editsearch = (SearchView) findViewById(R.id.searchView);
        editsearch.setOnQueryTextListener(this);

        Button buttonNewTheme = (Button) findViewById(R.id.validNewTheme);
        buttonNewTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newTheme = (EditText) findViewById(R.id.newTheme);

                if (newTheme.getText() != null && newTheme.getText().toString() != null && newTheme.getText().toString().length() >= 3) {
                    if (createQuestion != null && createQuestion.equals(new String("CREATE_QUESTION"))) {
                        Intent myIntent = new Intent(ChooseTheme.this, CreateQuestion.class);
                        List<Theme> themeListChoosed = new ArrayList<Theme>();
                        themeListChoosed.add(new Theme(newTheme.getText().toString()));
                        myIntent.putExtra(THEME, (ArrayList<Theme>) themeListChoosed);
                        myIntent.putExtra(USER, connectedUser);
                        startActivity(myIntent);
                    }
                } else {
                    Toast.makeText(ChooseTheme.this, "Un nouveau thème doit comporter au moins 3 caractères", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(ChooseTheme.this, Home.class);
        // Go to Home to prevent beug
        // Add connectedUser
        intent.putExtra(USER, connectedUser);
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
            case R.id.home:
                intent = new Intent(ChooseTheme.this, Home.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.friends:
                FriendsTask friends = new FriendsTask(ChooseTheme.this);
                friends.execute(connectedUser.getPseudo());
                break;
            case R.id.findFriend:
                intent = new Intent(ChooseTheme.this, ChooseFriends.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.chat:
                intent = new Intent(ChooseTheme.this, Chat.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.findQuiz:
                intent = new Intent(ChooseTheme.this, FindQuizz.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.evalMode:
                intent = new Intent(ChooseTheme.this, EvalMode.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.createEvaluation:
                intent = new Intent(ChooseTheme.this, ChooseQuizzEval.class);
                intent.putExtra(USER, connectedUser);
                startActivity(intent);
                finish();
                break;
            case R.id.logout:
                // Destroy user and return to main activity
                connectedUser = null;
                Toast.makeText(this, "A bientôt !", Toast.LENGTH_LONG).show();
                intent = new Intent(ChooseTheme.this, MainActivity.class);
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