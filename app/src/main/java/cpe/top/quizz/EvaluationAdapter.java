package cpe.top.quizz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import cpe.top.quizz.asyncTask.EvaluationTask;
import cpe.top.quizz.asyncTask.responses.AsyncResponse;
import cpe.top.quizz.beans.Evaluation;
import cpe.top.quizz.beans.User;

/**
 * Created by Camille on 18/01/2017.
 */

public class EvaluationAdapter extends BaseAdapter implements AsyncResponse {

    private static final String USER = "USER";
    private static final String EVAL_ID = "EVAL_ID";

    private List<Evaluation> listS;

    private Context mContext;

    private LayoutInflater mInflater;

    private User connectedUser;

    public EvaluationAdapter(Context context, List<Evaluation> aListQ, User connectedUser) {
        this.mContext = context;
        this.listS = aListQ;
        this.mInflater = LayoutInflater.from(mContext);
        this.connectedUser = connectedUser;
    }
    
    @Override
    public int getCount() {
        return listS.size();
    }

    @Override
    public Evaluation getItem(int position) {
        return listS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;
        Evaluation s = (Evaluation) listS.get(position);
        
        if (convertView == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.evalquizz_list_layout, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView evalName = (TextView)layoutItem.findViewById(R.id.evalName);
        TextView date = (TextView)layoutItem.findViewById(R.id.date);

        evalName.setText(s.getQuizzName());

        Date myDate = s.getDeadLine();
        int month = myDate.getMonth()+1;
        date.setText("Deadline : " + myDate.getDate() + "/" + month);

        addListenerToLayout(s, layoutItem);

        return layoutItem;
    }

    private void addListenerToLayout(final Evaluation e, LinearLayout layout) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EvalResult.class);
                intent.putExtra(USER, connectedUser);
                intent.putExtra(EVAL_ID, e.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void processFinish(Object obj) {

    }
}
