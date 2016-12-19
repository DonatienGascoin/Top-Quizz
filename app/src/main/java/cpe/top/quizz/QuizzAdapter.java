package cpe.top.quizz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cpe.top.quizz.asyncTask.QuizzDeleteTask;
import cpe.top.quizz.asyncTask.QuizzTask;
import cpe.top.quizz.asyncTask.responses.AsyncQuizzResponse;
import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;

/**
 * @author Romain Chazottier
 * @version 0.1
 * @since 25/11/2016
 */
public class QuizzAdapter extends BaseAdapter implements AsyncQuizzResponse {
    private static final String QUIZZ = "QUIZZ";

    private static final String LIST_QUIZZ = "LIST_QUIZZ";

    private static final String QUIZZ_DELETE_TASK = "QuizzDeleteTask";

    private final static String USER = "USER";

    private List<Quizz> listQ;

    private Context mContext;

    private LayoutInflater mInflater;

    private User connectedUser;

    public QuizzAdapter(Context context, List<cpe.top.quizz.beans.Quizz> aListQ, User connectedUser) {
        this.mContext = context;
        this.listQ = aListQ;
        this.mInflater = LayoutInflater.from(mContext);
        this.connectedUser = connectedUser;
    }

    public int getCount() {
        return listQ.size();
    }

    public Object getItem(int position) {
        return listQ.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;

        if (convertView == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.quizz_list_layout, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView name = (TextView)layoutItem.findViewById(R.id.name);
        TextView theme = (TextView)layoutItem.findViewById(R.id.theme);
        TextView del = (TextView)layoutItem.findViewById(R.id.del);

        Quizz q = (Quizz) listQ.get(position);
        name.setText(q.getName());
        // Theme of quizz on view = 1st theme of the 1st question
        List<Question> lQ = new ArrayList<Question>(q.getQuestions());
        List<Theme> lT = new ArrayList<Theme>(lQ.get(0).getThemes());
        theme.setText(lT.get(0).getName());

        addListenerToLayout(q, layoutItem);
        addListenerToDelTextView(q, del);

        return layoutItem;
    }

    /**
     * Private method used to add a listener on click to a specified layout
     * @param layout a layout
     */
    private void addListenerToLayout(final Quizz q, LinearLayout layout) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizzTask task = new QuizzTask(QuizzAdapter.this);
                task.execute(q.getName().toString());
            }
        });
    }

    private void addListenerToDelTextView(final Quizz q, TextView del) {
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizzDeleteTask task = new QuizzDeleteTask(QuizzAdapter.this);
                task.execute(String.valueOf(q.getId()), connectedUser.getPseudo());
            }
        });
    }

    @Override
    public void processFinish(Object obj) {
        try {
            if (((List<Object>) obj).get(1) != null && ((List<Object>) obj).get(1).equals(QUIZZ_DELETE_TASK)) { // Case of QuizzDeleteTask
                switch (((ReturnObject) ((List<Object>) obj).get(0)).getCode()) {
                    case ERROR_000:
                        Intent myIntent = new Intent(mContext, Home.class);
                        List<cpe.top.quizz.Quizz> quizzes = (List<cpe.top.quizz.Quizz>) ((List<ReturnObject>) obj).get(2).getObject();
                        myIntent.putExtra(LIST_QUIZZ, (ArrayList<cpe.top.quizz.Quizz>) quizzes);
                        myIntent.putExtra(USER, (User) connectedUser);
                        ((Activity) mContext).finish();
                        mContext.startActivity(myIntent);
                        Toast.makeText(mContext, "Suppression du quiz", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR_200:
                        Toast.makeText(mContext, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(mContext, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        } catch (ClassCastException e) {
            switch (((ReturnObject) obj).getCode()) {
                case ERROR_000:
                    Intent myIntent = new Intent(mContext, StartQuizz.class);
                    myIntent.putExtra(QUIZZ, (Quizz) ((ReturnObject) obj).getObject());
                    mContext.startActivity(myIntent);
                    break;
                case ERROR_200:
                    Toast.makeText(mContext, "Impossible d'acceder au serveur", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_100:
                default:
                    Toast.makeText(mContext, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
