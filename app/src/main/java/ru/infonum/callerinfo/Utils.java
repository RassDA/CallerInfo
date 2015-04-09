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


    public static String requestAppSettings() {
        String s = postString(APP_PARAMETERS_PAGE_URL, "", PLUS_SIGN + APP_PARAMETERS_PAGE_NAME);
        //st += " o=" + s;//-----------------------------

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
        //ищет текстографически последний объект в {} в строке.
        //TODO неправильно, что находит вложенный вместо объекта верхнего уровня

        String tmp = s;
        String out = "";
        int p1;
        int p2;

        if (s != null && s.length() > 1) {
            do {

                p1 = tmp.lastIndexOf("{");
                if(p1 >=  0 ){
                    p2 = tmp.lastIndexOf("}");
                    if(p2 > 0){
                        if(p1<p2){
                            out = s.substring(p1,p2+1);
                            out = out.replace("\\" + "\"", "\"");
                            //st += " k=" + out;//-----------------------------

                            break;
                        }
                        tmp=tmp.substring(0, p2+1);
                    }
                }

            } while (p1 >= 0);
        }

        return out;
    }


    public static String selectJsonActiveObj(String s) {
        //последний непустой объект после последнего пустого, если пустой есть
        //зачем?
        String string = "";
        String out= "";

        do {
            string = selectJsonLastObj(s);

            string = s.substring(0, s.lastIndexOf(string));
            if (string.length() == 2) {

            }
        } while(false);

        return s;
    }


    public static JSONObject strToNewJsonObj(String s) {
        st += " n=" + s;//-----------------------------------
        try {

            jsonObject = new JSONObject(s);
            //st += " l=" + jsonObject.toString();//-----------------------------


        } catch (JSONException e) {
            Log.debug("\nError parsing data " + e.toString());
        }
        //st += " m=" + jsonObject.toString();//-----------------------------

        return jsonObject;
    }


}