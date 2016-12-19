package cpe.top.quizz.asyncTask;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;


import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.User;
import cpe.top.quizz.utils.QuizzUtils;
import cpe.top.quizz.utils.UserUtils;

/**
 * @author Maxence Royer
 * @since 05/12/2016
 * @version 0.2
 */

public class ConnexionTask extends AsyncTask<String, Integer, List<ReturnObject>>  {
    public AsyncUserResponse delegate=null;

    private String pseudo, password;

    public ConnexionTask(AsyncUserResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected List<ReturnObject> doInBackground(String... params) {
        List<ReturnObject> listReturnO = new ArrayList<ReturnObject>();
        // Used to return the User from the API
        ReturnObject u = UserUtils.checkCredentials(params[0], params[1]);
        listReturnO.add(u);
        // Used to return the quizzes of the User from the API
        ReturnObject listQ = QuizzUtils.getAllQuizzByUser(params[0]);
        listReturnO.add(listQ);
        return (listReturnO != null) ? listReturnO : null;
    }

    @Override
    protected void onPostExecute(List<ReturnObject> result) {
        delegate.processFinish(result);
    }
}