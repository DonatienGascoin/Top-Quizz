package cpe.top.quizz.utils;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cpe.top.quizz.beans.Quizz;
import cpe.top.quizz.beans.ReturnCode;
import cpe.top.quizz.beans.ReturnObject;

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
                q = ParseUtils.getQuizzFromReturnObject(obj);
            }
            return q;
        } catch (JSONException e) {
            return null;
        }
    }

    @Nullable
    public static ReturnObject getAllQuizzByUser(String pseudo) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("pseudo", pseudo);
        JSONObject jsonQuizz = getJSONFromUrl("quizz/getAllQuizzesByPseudo/", key);
        ReturnObject rO = new ReturnObject();
        List<Quizz> listQuizzes = new ArrayList<Quizz>();

        try {
            ReturnCode rC = ReturnCode.valueOf((String) jsonQuizz.get("code"));

            if (rC.equals(ReturnCode.ERROR_000)) {
                JSONArray jObject = jsonQuizz.getJSONArray("object");

                for (int i=0; i< jObject.length(); i++) {
                    JSONObject currentElement = jObject.getJSONObject(i);
                    Quizz q = new Quizz();
                    q.setId((int) currentElement.getInt("id"));
                    q.setName((String) currentElement.get("name"));
                    q.setIsVisible((String) currentElement.get("isVisible"));
                    q.setQuestions(ParseUtils.getQuestionsFromJsonArray(currentElement.getJSONArray("questions")));
                    listQuizzes.add(q);
                }

                rO.setObject(listQuizzes);
            }

            rO.setCode(rC);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (rO != null) ? rO : null;
    }

    @Nullable
    public static ReturnObject deleteQuizz(Integer id) {
        Map<String, String> key = new LinkedHashMap<>();
        key.put("id", String.valueOf(id));
        JSONObject obj = getJSONFromUrl("quizz/deleteQuizzById/", key);

        ReturnObject rO = new ReturnObject();
        ReturnCode rC = null;
        try {
            rC = ReturnCode.valueOf((String) obj.get("code"));
            rO.setCode(rC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rO;
    }
}
