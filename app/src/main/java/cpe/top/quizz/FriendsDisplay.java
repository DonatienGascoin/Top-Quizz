package cpe.top.quizz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cpe.top.quizz.beans.User;

public class FriendsDisplay extends AppCompatActivity {

    private static final String USER = "USER";
    private static final String LIST_FRIENDS = "LIST_FRIENDS";
    private User connectedUser;
    private List<User> listFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_display);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        if (intent != null && intent.getSerializableExtra(USER) != null && intent.getSerializableExtra(LIST_FRIENDS) != null && ((List<User>) intent.getSerializableExtra(LIST_FRIENDS)).size() != 0) {
            this.listFriends = (List<User>) intent.getSerializableExtra(LIST_FRIENDS);
            this.connectedUser = (User) intent.getSerializableExtra(USER);

            // Adapter
            FriendsAdapter adapter = new FriendsAdapter(this, listFriends, connectedUser);

            // The list (IHM)
            ListView list = (ListView) findViewById(R.id.listFriends);

            // Initialization of the list
            list.setAdapter(adapter);
        } else { // No Friends
            LinearLayout divFriends = (LinearLayout) findViewById(R.id.divFriends);
            divFriends.removeAllViews();

            TextView noQuiz = new TextView(this);
            noQuiz.setText("Aucun ami dans votre liste...");
            noQuiz.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            noQuiz.setTextSize(20);
            noQuiz.setGravity(Gravity.CENTER);
            noQuiz.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            divFriends.addView(noQuiz);
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
                Intent intent = new Intent(FriendsDisplay.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}