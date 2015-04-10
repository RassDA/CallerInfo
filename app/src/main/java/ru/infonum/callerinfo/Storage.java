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
    static boolean debug = false;
    static String[] resourcesArray;


    public static String getByName(String name) {
        String out = "";
        String val = "";
        //name = "\"" + name + "\"";

        st += " name=" + name + ";";
        out = context.getString(context.getResources().getIdentifier(name, "string", context.getPackageName()));
        if (jsonObject != null) {
            try {
                val = jsonObject.getString(name);
                st += "v=" + val + ";";
            } catch (JSONException e) {
                val = "";
            }
            if (val != null && !val.equals("")) {
                out = val;
            }
        }else st += "json=null";

        st += out;//-----------------------------
        return out;
    }




}

