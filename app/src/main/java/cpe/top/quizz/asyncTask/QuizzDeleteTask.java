package cpe.top.quizz.asyncTask;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import cpe.top.quizz.asyncTask.responses.AsyncQuizzResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.utils.QuizzUtils;

/**
 * @author Maxence Royer
 * @since 08/12/2016
 * @version 0.1
 */
public class QuizzDeleteTask extends AsyncTask<String, Integer, List<Object>> {
    public AsyncQuizzResponse delegate = null;

    private String name;

    public QuizzDeleteTask(AsyncQuizzResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected List<Object> doInBackground(String... params) {
        List<Object> lObject = new ArrayList<Object>();
        ReturnObject rO = QuizzUtils.deleteQuizz(new Integer(params[0]));
        // ReturnObject - deleteQuizz API
        lObject.add(rO);
        // String to test if we are in case of QuizzDeleteTask and not QuizzTask
        lObject.add(new String("QuizzDeleteTask"));
        // The updated quiz's list of an user
        ReturnObject listQ = QuizzUtils.getAllQuizzByUser(params[1]);
        lObject.add(listQ);
        return (lObject != null) ? lObject : null;
    }

    @Override
    protected void onPostExecute(List<Object> result) {
        delegate.processFinish(result);
    }
}