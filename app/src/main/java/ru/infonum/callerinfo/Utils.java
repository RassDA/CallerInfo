package ru.infonum.callerinfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.ExecutionException;

/**
 * Created by d1i on 03.04.15.
 */
public class Utils {
    static final String APP_PARAMETERS_PAGE_NAME ="00001110001";
    static final String APP_PARAMETERS_PAGE_URL ="http://checkin.infonum.ru/api/read.php/";


    public static String requestSettings(){
        String s = postString(APP_PARAMETERS_PAGE_URL, "", "+" + APP_PARAMETERS_PAGE_NAME);
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

        //Toast.makeText(context, "[1]=" + param1 + " [2]=" + param2, Toast.LENGTH_LONG).show();

        return response;
    }



}

