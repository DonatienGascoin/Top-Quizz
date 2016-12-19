package cpe.top.quizz.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.Response;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;

/**
 * @author Maxence Royer
 * @version 0.1
 * @since 05/12/2016
 */
public class ParseUtils {
    /**
     * Get all Questions from JSON
     * <p>
     * WARNING: Possible infinite loop !
     * A quizz contain a Question collection, and a question a collection of quizz
     *
     * @return {@link Collection < Question >}
     * @throws JSONException
     */
    protected static Collection<Question> getQuestionsFromJsonArray(JSONArray questionsArray) throws JSONException {
        Collection<Question> questions = new ArrayList<>();
        if (questionsArray.length() != 0) {
            for (int i = 0; i < questionsArray.length(); i++) {

                JSONObject tmpObj = questionsArray.getJSONObject(i);

                JSONArray responsesArray = tmpObj.getJSONArray("responses");
                Collection<Response> responses = null;
                if (responsesArray.length() != 0) {
                    responses = getResponsesFromJsonArray(responsesArray);
                }

                JSONArray themesArray = tmpObj.getJSONArray("themes");
                Collection<Theme> themes = null;
                if (themesArray.length() != 0) {
                    themes = getThemesFromJsonArray(themesArray);
                }

                Question questionTmp = new Question(tmpObj.getString("label"), tmpObj.getString("pseudo"), tmpObj.getString("explanation"), responses, themes, null);
                questions.add(questionTmp);
            }
        }

        return questions;
    }

    /**
     * Get all Friends ({@link User}) from JSON
     * <p>
     * WARNING:
     * Only one loop: A user only can see his own friends, not friends to his friends. Idem for his friends'questions
     * On this method, we only keep friends ids
     * <p>
     * If you want to search further, create new method or use recursion (friends was collection of user)
     *
     * @return {@link Collection<User>}
     * @throws JSONException
     */
    protected static Collection<User> getFriendsFromJsonArray(JSONArray friendsArray) throws JSONException {
        Collection<User> friends = new ArrayList<>();
        if (friendsArray.length() != 0) {
            for (int i = 0; i < friendsArray.length(); i++) {
                JSONObject tmpObj = friendsArray.getJSONObject(i);
                User userTmp = new User(tmpObj.getString("pseudo"));
                friends.add(userTmp);
            }
        }
        return friends;
    }

    /**
     * Get all Responses from JSON
     *
     * @return @return {@link Collection<Response>}
     * @throws JSONException
     */
    protected static Collection<Response> getResponsesFromJsonArray(JSONArray responsesArray) throws JSONException {
        Collection<Response> responses = new ArrayList<>();
        if (responsesArray.length() != 0) {
            for (int i = 0; i < responsesArray.length(); i++) {
                JSONObject tmpResponse = responsesArray.getJSONObject(i);
                Response response = new Response(tmpResponse.getString("label"), tmpResponse.getBoolean("isValide"));
                responses.add(response);
            }
        }
        return responses;
    }

    /**
     * Get all Themes from JSON
     *
     * @return @return {@link Collection<Theme>}
     * @throws JSONException
     */
    protected static Collection<Theme> getThemesFromJsonArray(JSONArray themeArray) throws JSONException {
        Collection<Theme> themes = new ArrayList<>();
        if (themeArray.length() != 0) {
            for (int i = 0; i < themeArray.length(); i++) {
                JSONObject tmpTheme = themeArray.getJSONObject(i);
                Theme theme = new Theme(tmpTheme.getInt("id"), tmpTheme.getString("name"));
                themes.add(theme);
            }
        }
        return themes;
    }

    /**
     * Get all Quizz from JSON
     * <p>
     * WARNING: Possible infinite loop !
     * A quizz contain a Question collection, and a question a collection of quizz
     *
     * @return @return {@link Collection< Quizz >}
     * @throws JSONException
     */
    protected static Collection<Quizz> getQuizzsFromJsonArray(JSONArray quizzsArray) throws JSONException {
        Collection<Quizz> quizzs = new ArrayList<>();
        if (quizzsArray.length() != 0) {
            for (int i = 0; i < quizzsArray.length(); i++) {
                JSONObject tmpQuizz = quizzsArray.getJSONObject(i);

                JSONArray questionArray = tmpQuizz.getJSONArray("questions");
                Collection<Question> questions = null;
                if (questionArray.length() != 0) {
                    questions = getQuestionsFromJsonArray(questionArray);
                }

                quizzs.add(new Quizz(tmpQuizz.getInt("id"), tmpQuizz.getString("name"), tmpQuizz.getString("isVisible"), questions));
            }
        }
        return quizzs;
    }

    /**
     * Used to return a Quizz from a ReturnObject
     * @param obj
     * @return a Quizz
     * @throws JSONException
     */
    protected static Quizz getQuizzFromReturnObject(JSONObject obj) throws JSONException {
        JSONObject jsonQuizz = obj.getJSONObject("object");

        JSONArray questionsArray = jsonQuizz.getJSONArray("questions");
        Collection<Question> questions = null;
        if (questionsArray.length() != 0) {
            questions = getQuestionsFromJsonArray(questionsArray);
        }

        return new Quizz(jsonQuizz.getInt("id"), jsonQuizz.getString("name"), jsonQuizz.getString("isVisible"), questions);
    }
}
