package ru.infonum.callerinfo;

import android.content.Context;
import android.net.Uri;

import org.json.JSONException;
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


    public static String getIntResByName(String resStrName) {
        String val = "";

        int ids = context.getResources().getIdentifier(resStrName, "string", context.getPackageName());
        val = context.getString(ids);
        return val;
    }


    public static String getExtResByName(String resStrName) {
        String val;
        try {
            val = js.getString(resStrName);
        }catch(JSONException e) {
            val = ""; //
        }
        return val;

    }


    public static String getByName(String resStrName) {
        String s = "";
        if (js != null) {
            s = getExtResByName(resStrName);
        }
        if(s.equals("")){
           s =  getIntResByName(resStrName);
        }
        return s;
    }


    public static void InitExtRes() {

        //читаем с сайта набор пар в объект
        String s = requestAppSettings();
        if (! s.equals("")) {
            s = selectJsonLastObj(s);
            js = toJsonObj(s);          // объект там
            extInitialized = true;
        }else {
            js = null;
            extInitialized = false;
        }
    }


    public static String[] InitIntRes() {
        //задача: получить объект с парами

        String[] sa = context.getResources().getStringArray(R.array.string_array_name);

        return sa;
    }

    public static void InitRes() {
        InitExtRes();

    }








}