package ru.infonum.callerinfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static ru.infonum.callerinfo.Storage.*;

/**
 * Created by d1i on 03.04.15.
 */
public class Utils {
    static final String APP_PARAMETERS_PAGE_NAME = "00001110001"; //случайный несуществующий номер телефона
    static final String APP_PARAMETERS_PAGE_URL = "http://checkin.infonum.ru/api/read.php/";
    static final String PLUS_SIGN = "+"; // не забывать добавлять к телефону в параметре пост-запроса!!!
    static JSONObject jsonObject;


    public static String postString(String url, String param1, String param2) {

        String response = "";
        httpPost httpPost = new httpPost();
        httpPost.execute(url, param1, param2); // post-запрос по этому адресу и этими пост-параметрами: текст/телефон

        try {
            response = httpPost.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (response == null) response = "";

        return response;
    }


    public static String requestAppSettings() {
        String s = postString(APP_PARAMETERS_PAGE_URL, "", PLUS_SIGN + APP_PARAMETERS_PAGE_NAME);
        if (debug) st += " o=" + s;//-----------------------------

        return s;
    }


    public static String selectJsonLastObj(String s) {
        //ищет текстографически последний объект в {} в строке.
        //TODO неправильно, что находит вложенный вместо объекта верхнего уровня

        String out = "";
        int p1 = 0;
        int p2 = 0;
        String tmp = s;
        if (s != null && s.length() > 1) {
            do {
                p1 = tmp.lastIndexOf("{");
                if(p1 >=  0 ){                          //есть откр скобка
                    p2 = tmp.lastIndexOf("}");
                    if(p2 > 0){                         //есть закр скобка
                        if(p1<p2){                      //скобки парные
                            out = tmp.substring(p1, p2 + 1);
                            out = out.replace("\\" + "\"", "\"");
                            //out = out.replace(" ", "");
                            if (debug) st += " k=" + out;//-----------------------------

                            break;
                        }
                        tmp=tmp.substring(0, p2+1);     //обе скобки есть, но закр скобка стоит впереди - берем подстроку до нее вкл
                        if (debug) st += " substr=" + tmp;//-----------------------------

                    }
                }

            } while (p1 >= 0 && p2 > 0); //пока в подстроке есть обе скобки
        }

        return out;
    }



    public static JSONObject strToNewJsonObj(String s) {

        if (debug) st += " n=" + s;//-----------------------------------

        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            Log.debug("\nError parsing data " + e.toString());
        }

        return jsonObject;
    }


}