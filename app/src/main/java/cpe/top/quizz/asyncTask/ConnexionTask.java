package cpe.top.quizz.asyncTask;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.utils.QuizzUtils;
import cpe.top.quizz.utils.UserUtils;

/**
 * @author Maxence Royer
 * @since 05/12/2016
 * @version 0.2
 */

public class ConnexionTask extends AsyncTask<String, Integer, ReturnObject>  {
    public AsyncUserResponse delegate=null;

    private String pseudo, password;

    public ConnexionTask(AsyncUserResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected ReturnObject doInBackground(String... params) {
        // Used to return the User from the API
        return UserUtils.checkCredentials(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(ReturnObject result) {
        delegate.processFinish(result);
    }
}