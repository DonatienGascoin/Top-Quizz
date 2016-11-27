package cpe.top.quizz.asyncTask.responses;

import java.util.Objects;

import cpe.top.quizz.beans.User;

/**
 *
 * Interface which allow to get response to AsyncTask
 *
 * @author Donatien
 * @since 08/11/2016
 * @version 0.1
 */

public interface AsyncUserResponse {
    void processFinish(Object obj);
}