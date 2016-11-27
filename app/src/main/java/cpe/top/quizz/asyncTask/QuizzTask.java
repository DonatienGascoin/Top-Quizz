package cpe.top.quizz.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import cpe.top.quizz.asyncTask.responses.AsyncQuizzResponse;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.ReturnCode;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.utils.QuizzUtils;

/**
 * @author Maxence Royer
 * @since 21/11/2016
 * @version 0.1
 */

public class QuizzTask extends AsyncTask<String, Integer, ReturnObject> {
    public AsyncQuizzResponse delegate = null;

    private String name;

    public QuizzTask(AsyncQuizzResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected ReturnObject doInBackground(String... params) {
        Quizz quizz = QuizzUtils.getQuizz(params[0]);
        ReturnObject rO = new ReturnObject();
        rO.setObject(quizz);
        rO.setCode(ReturnCode.ERROR_000);
        return (rO != null) ? rO : null;
    }

    @Override
    protected void onPostExecute(ReturnObject result) {
        delegate.processFinish(result);
    }
}