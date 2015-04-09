package ru.infonum.callerinfo;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import static ru.infonum.callerinfo.Utils.jsonObject;
import static ru.infonum.callerinfo.Utils.requestAppSettings;
import static ru.infonum.callerinfo.Utils.selectJsonLastObj;
import static ru.infonum.callerinfo.Utils.strToNewJsonObj;

/**
 * Created by d1i on 08.04.15.
 */
public class Storage {
    static Context context;
    static String st;
    static JSONObject js;

    public static String getIntResByName(String resStrName) {
        String val = "";

        int ids = context.getResources().getIdentifier(resStrName, "string", context.getPackageName());
        val = context.getString(ids);
        return val;
    }


    public static String getExtResByName(String resStrName) {
        String val = "";
        if (jsonObject != null) {
            //initExtRes(); //перечитывать с сайта каждый раз заново
            try {
                val = jsonObject.getString(resStrName);
                st += " d=" + val; //--------------------------------------------
            } catch (JSONException e) {
                //
                val = "";
            }
        }
        return val;

    }


    public static String getByName(String name) {
        String val = "";
        String tmp = "";
        st += " j=" + name;//-----------------------------

        val = getIntResByName(name); //работает

        if (jsonObject != null) {
            tmp = getExtResByName(name);
            st += " b=" + tmp;//--------------------------------------------
            if (!tmp.equals("")) {
                val = tmp;
                st += " c=" + val;//--------------------------------------------
            }
        }

        return val;
    }


    public static void initExtRes() {

        //читаем с сайта набор пар в объект
        String s = requestAppSettings();
        st += " g=" + s;//-----------------------------

        if (! s.equals("")) {

            s = selectJsonLastObj(s);
            st += " h=" + s;//-----------------------------

            jsonObject = strToNewJsonObj(s);          // объект там
            st += " i=" + jsonObject.toString();//-----------------------------

        }

    }


    public static String[] initIntRes() {
        //задача: получить объект с парами
        // массив для тестов
        String[] sa = context.getResources().getStringArray(R.array.string_array_name);

        return sa;
    }

    public static void initRes(Context ctx) {
        context = ctx;
        initExtRes();
        st += "e ";//-----------------------------
        initIntRes();
        st += "f ";//-----------------------------

    }








}