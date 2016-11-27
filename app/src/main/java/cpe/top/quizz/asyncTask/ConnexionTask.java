package cpe.top.quizz.asyncTask;

import android.os.AsyncTask;

import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.utils.UserUtils;

/**
 *
 * @author Donatien
 * @since 08/11/2016
 * @version 0.1
 */

public class ConnexionTask extends AsyncTask<String, Integer, ReturnObject>

{
    public AsyncUserResponse delegate=null;

    private String pseudo, password;

    public ConnexionTask(AsyncUserResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected ReturnObject doInBackground(String... params) {
        ReturnObject u = UserUtils.checkCredentials(params[0], params[1]);
        return (u != null) ? u : null;
    }

    @Override
    protected void onPostExecute(ReturnObject result) {
        delegate.processFinish(result);
    }
}