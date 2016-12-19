package cpe.top.quizz.asyncTask;

import android.os.AsyncTask;

import cpe.top.quizz.asyncTask.responses.AsyncUserResponse;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.User;
import cpe.top.quizz.utils.UserUtils;

/**
 * @author Donatien
 * @version 0.1
 * @since 08/11/2016
 */

public class AddUserTask extends AsyncTask<String, Integer, ReturnObject> {

    public AsyncUserResponse delegate = null;

    public AddUserTask(AsyncUserResponse asyncResponse) {
        delegate = asyncResponse;
    }
    /**
     * Using with:
     * params[0]: Pseudo
     * params[1]: Mail
     * params[2]: Password
     *
     * @param params
     * @return {@link User}
     */
    @Override
    protected ReturnObject doInBackground(String... params) {
        User u = new User();

        u.setPseudo(params[0]);
        u.setMail(params[1]);
        u.setPassword(params[2]);

        // Add user
        ReturnObject result = UserUtils.addUser(u);
        return (result != null) ? result : null;
    }

    @Override
    protected void onPostExecute(ReturnObject result) {
        delegate.processFinish(result);
    }
}
