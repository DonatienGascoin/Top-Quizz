package cpe.top.quizz.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.Response;
import cpe.top.quizz.beans.ReturnCode;
import cpe.top.quizz.beans.ReturnObject;
import cpe.top.quizz.beans.Theme;
import cpe.top.quizz.beans.User;

/**
 * @author Donatien
 * @version 0.1
 * @since 07/11/2016
 */

public class UserUtils extends JsonParser {

    public UserUtils() {

    }

    @Nullable
    public static User getUser(String pseudo) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("pseudo", pseudo);
        JSONObject obj = getJSONFromUrl("user/get/", key);

        try {
            User u = null;
            if (obj != null) {
                u = getUserFromReturnObject(obj);
            }
            return u;
        } catch (JSONException e) {
            return null;
        }
    }

    @Nullable
    public static ReturnObject checkCredentials(String pseudo, String password) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("pseudo", pseudo);
        key.put("password", password);
        JSONObject obj = getJSONFromUrl("user/checkCredentials/", key);
        ReturnObject object = new ReturnObject();

        try {
            object.setCode(ReturnCode.valueOf(obj.getString("code")));
            User u = null;
            if (obj != null) {
                u = getMinimumUserFromReturnObject(obj);
            } else {
                object.setCode(ReturnCode.ERROR_200);
            }
            object.setObject(u);
        } catch (RuntimeException e) {
            object.setCode(ReturnCode.ERROR_200);
            Log.e("Runtime", "", e);
        } catch (JSONException e) {
            Log.e("JSON", "", e);
            object.setCode(ReturnCode.ERROR_200);
        }
        return object;
    }

    @Nullable
    public static User getByMail(String mail) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("mail", mail);
        JSONObject obj = getJSONFromUrl("user/getByMail/", key);

        try {
            User u = null;
            if (obj != null) {
                u = new User(obj.getString("pseudo"), obj.getString("mail"), obj.getString("password"), null, null);
            }
            return u;
        } catch (JSONException e) {
            Log.e("JSON", "", e);
            return null;
        }
    }

    @Nullable
    public static ReturnObject addUser(User u) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("pseudo", u.getPseudo());
        key.put("mail", u.getMail());
        key.put("password", u.getPassword());
        JSONObject obj = getJSONFromUrl("user/add/", key);
        ReturnObject object = new ReturnObject();

        try {
            object.setCode(ReturnCode.valueOf(obj.getString("code")));
            User user = null;
            if (obj != null) {
                u = getMinimumUserFromReturnObject(obj);
            }
            object.setObject(u);
        } catch (RuntimeException e) {
            object.setCode(ReturnCode.ERROR_200);
            Log.e("Runtime", "", e);
        } catch (JSONException e) {
            Log.e("JSON", "", e);
            object.setCode(ReturnCode.ERROR_200);
        }
        return object;
    }

    @Nullable
    public static ReturnObject changePassword(String password, String mail) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("password", password);
        key.put("mail", mail);
        JSONObject obj = getJSONFromUrl("user/changePassword/", key);
        ReturnObject object = new ReturnObject();
        try {
            object.setCode(ReturnCode.valueOf(obj.getString("code")));
            User u = null;
            if (obj != null) {
                u = getMinimumUserFromReturnObject(obj);
            }
            object.setObject(u);
        } catch (RuntimeException e) {
            object.setCode(ReturnCode.ERROR_200);
            Log.e("Runtime", "", e);
        } catch (JSONException e) {
            Log.e("JSON", "", e);
            object.setCode(ReturnCode.ERROR_200);
        }
        return object;
    }

    /**
     * Basic user with arguments:
     * - Pseudo
     * - Mail
     * - Other elements was set to null
     *
     * @return {@link User}
     * @throws JSONException
     */
    private static User getMinimumUserFromReturnObject(JSONObject obj) throws JSONException {
        //Get object variable in ReturnObject
        JSONObject jsonUser = obj.getJSONObject("object");

        return new User(jsonUser.getString("pseudo"), jsonUser.getString("mail"), null, null, null);
    }

    /**
     * User with all arguments:
     * Warning:
     * Depending on the JSON sent, the function can make an infinite loop.
     *
     * @return {@link User}
     * @throws JSONException
     */
    protected static User getUserFromReturnObject(JSONObject obj) throws JSONException {
        //Get object variable in ReturnObject
        JSONObject jsonUser = obj.getJSONObject("object");

        JSONArray friendsArray = jsonUser.getJSONArray("friends");
        Collection<User> friends = null;
        if (friendsArray.length() != 0) {
            friends = getFriendsFromJsonArray(friendsArray);
        }

        JSONArray questionArray = jsonUser.getJSONArray("questions");
        Collection<Question> questions = null;
        if (questionArray.length() != 0) {
            questions = getQuestionsFromJsonArray(questionArray);
        }

        return new User(jsonUser.getString("pseudo"), jsonUser.getString("mail"), null, friends, questions);
    }

    /**
     * Get all Questions from JSON
     * <p>
     * WARNING: Possible infinite loop !
     * A quizz contain a Question collection, and a question a collection of quizz
     *
     * @return {@link Collection<Question>}
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
     * @return @return {@link Collection<Quizz>}
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
}
