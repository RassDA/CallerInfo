package ru.infonum.callerinfo;

import android.content.Context;

import org.json.JSONObject;

import static ru.infonum.callerinfo.Utils.requestAppSettings;
import static ru.infonum.callerinfo.Utils.selectJsonLastObj;
import static ru.infonum.callerinfo.Utils.toJsonObj;

/**
 * Created by d1i on 08.04.15.
 */
public class Storage {
    static Context context;

    static boolean fromSite = false;
    static boolean extInitialized = false;
    static boolean innerInitialized = false;
    static String[] array;
    static JSONObject js;


    public static void setMyContext(Context ctx) {
        context = ctx;
    }


    public static String getInnerResStrValByName(String resStrName) {
        String val = "";

        int ids = context.getResources().getIdentifier(resStrName, "string", context.getPackageName());
        val = context.getString(ids);
        return val;
    }

    public static String getExtResStrValByName(String resStrName) {
        String s = requestAppSettings();
        String[] sa = new String[100];
        array = sa;

        if (! s.equals("")) {
            s = selectJsonLastObj(s);
            js = toJsonObj(s);
            fromSite = true;
        }
        return "";

    }

    public static String getResStrValByName(String resStrName) {

        return getInnerResStrValByName(resStrName);
    }


    public static String getAppSettings(Context context) {

        String s = requestAppSettings();

        if (! s.equals("")) {
            s = selectJsonLastObj(s);
            js = toJsonObj(s);
            fromSite = true;
        }else{
            array = context.getResources().getStringArray(R.array.string_array_name);
        }


        return s;
    }


    public static void InitExtSettings() {

        //читаем с сайта набор пар в объект
        String s = requestAppSettings();
        if (! s.equals("")) {
            s = selectJsonLastObj(s);
            js = toJsonObj(s);          // объект там
            extInitialized = true;
        }else {
            extInitialized = false;
        }
    }

    public static String[] InitInnerSettings() {
        //задача: получить объект пар

        String[] sa = context.getResources().getStringArray(R.array.string_array_name);

        return sa;
    }



}