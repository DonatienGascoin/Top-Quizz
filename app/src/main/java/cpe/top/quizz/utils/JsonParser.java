package cpe.top.quizz.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Donatien
 * @version 1.0
 * @since 06/11/2016
 */

public class JsonParser {
    public static final String API_URL = "http://10.0.2.2:8090/";

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";


    public JsonParser() {

    }

    public static JSONObject getJSONFromUrl(String mehtod, Map<String, String> params) {

        URL url = null;
        try {
            url = new URL(API_URL + mehtod);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

            }
            String urlParameters = postData.toString();
            URLConnection conn = url.openConnection();
            Log.i("[INFO]",conn.toString());
            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(urlParameters);
            writer.flush();

            String result = "";
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = reader.readLine()) != null) {
                result += line;
            }
            writer.close();
            reader.close();
            System.out.println(result);

            // On récupère le JSON complet
            if(result != null){
                return (new JSONObject(result));
            }
            return null;



        } catch (MalformedURLException e) {
            Log.e("MalformedURL", "",e);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", "",e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("[BDD]", "Object does not exist in database",e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSON", "",e);
            e.printStackTrace();
        }
        return null;
    }

}
