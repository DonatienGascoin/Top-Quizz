package cpe.top.quizz.utils;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import cpe.top.quizz.beans.Question;
import cpe.top.quizz.beans.Quizz;

/**
 * @author Maxence Royer
 * @version 0.1
 * @since 21/11/2016
 */
public class QuizzUtils extends JsonParser {

    public QuizzUtils() {

    }

    @Nullable
    public static Quizz getQuizz(String name) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("name", name);
        JSONObject obj = getJSONFromUrl("quizz/getQuizzByName/", key);

        try {
            Quizz q = null;
            if (obj != null) {
                q = getQuizzFromReturnObject(obj);
            }
            return q;
        } catch (JSONException e) {
            return null;
        }
    }

    private static Quizz getQuizzFromReturnObject(JSONObject obj) throws JSONException {
        JSONObject jsonQuizz = obj.getJSONObject("object");

        JSONArray questionsArray = jsonQuizz.getJSONArray("questions");
        Collection<Question> questions = null;
        if (questionsArray.length() != 0) {
            questions = UserUtils.getQuestionsFromJsonArray(questionsArray);
        }

        return new Quizz(jsonQuizz.getInt("id"), jsonQuizz.getString("name"), jsonQuizz.getString("isVisible"), questions);
    }
}
