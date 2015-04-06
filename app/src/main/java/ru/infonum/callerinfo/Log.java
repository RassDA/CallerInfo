package ru.infonum.callerinfo;

public class Log {
    private final static String LOG_CODE ="CallNum--";

    public static void debug(String s){
        android.util.Log.d(LOG_CODE,s);
    }
}
