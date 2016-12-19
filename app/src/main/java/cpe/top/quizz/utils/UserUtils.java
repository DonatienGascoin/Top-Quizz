package cpe.top.quizz.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.ReturnCode;
import cpe.top.quizz.beans.ReturnObject;
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
            friends = ParseUtils.getFriendsFromJsonArray(friendsArray);
        }

        JSONArray questionArray = jsonUser.getJSONArray("questions");
        Collection<Question> questions = null;
        if (questionArray.length() != 0) {
            questions = ParseUtils.getQuestionsFromJsonArray(questionArray);
        }

        return new User(jsonUser.getString("pseudo"), jsonUser.getString("mail"), null, friends, questions);
    }
}
