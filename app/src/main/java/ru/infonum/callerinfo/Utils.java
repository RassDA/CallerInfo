package ru.infonum.callerinfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by d1i on 03.04.15.
 */
public class Utils {
    static final String APP_PARAMETERS_PAGE_NAME = "00001110001"; //случайный несуществующий номер телефона
    static final String APP_PARAMETERS_PAGE_URL = "http://checkin.infonum.ru/api/read.php/";
    static final String PLUS_SIGN = "+"; // не забывать добавлять к телефону в параметре пост-запроса!!!


    public static String requestAppSettings() {
        String s = postString(APP_PARAMETERS_PAGE_URL, "", PLUS_SIGN + APP_PARAMETERS_PAGE_NAME);
        s = selectJsonLastObj(s);

        return s;
    }


    public static String postString(String url, String param1, String param2) {

        String response = "";
        httpPost httpPost = new httpPost();
        httpPost.execute(url, param1, param2); // post-запрос по этому адресу и этими пост-параметрами: текст/телефон

        try {
            response = httpPost.get();
            if (response == null) response = "";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String selectJsonLastObj(String s) {
        String tmp = s;
        String out = s;
        int p1;
        int p2;

        if (s != null && s.length() > 1) {
            do {
                p1 = tmp.lastIndexOf("{");
                p2 = tmp.lastIndexOf("}");

                if (p1 > 0 && p2 > 0 && p1 < p2) {
                    out = tmp.substring(p1, p2 + 1);
                    break;
                } else {
                    out = "";
                }
                tmp = tmp.substring(0, p1 - 1);
            } while (p1 > 0 && p2 > 0 && p1 < p2);
        }

        return out.replace("\\" + "\"", "\"");
    }

    public static JSONObject toJsonObj(String s) {
        JSONObject jsonObject = null;

        try {
            jsonObject = (JSONObject) new JSONObject(s);
        } catch (JSONException e) {
            Log.debug("\nError parsing data " + e.toString());
        }

        return jsonObject;
    }




}