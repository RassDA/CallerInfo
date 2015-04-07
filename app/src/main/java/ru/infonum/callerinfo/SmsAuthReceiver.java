package ru.infonum.callerinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;

/**
 * Created by d1i on 07.04.15.
 * Created by d1i on 09.02.15.
 */



//TODO - Возвращать результат аутентификации в главную активность, кроме вывода сообщений в лог и во всплывающие окна.
//TODO - использовать время для оценки возможности аутентификации - текущее время + время смс.
//TODO - разобраться с записью смс в стандартное хранилище. Когда смс отправляет приложение - смс не сохраняется в отправленных.
//TODO - реализовать возможность вызова приложения через интент-фильтры, как приложение для аутентификации.
//TODO -

public class SmsAuthReceiver extends BroadcastReceiver {
    //private static final String AUTH_STR = "infonum.ru/smsauth";
    //private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public String msgTel = "";
    public String msgTxt = "";
    public long msgTimeL = 0L;
    public String msgTimeS = "";
    public String str = "";
    public final String TAG = "SmsAuth";
    public Timestamp timeNow;
    public long timeNowL;
    Context context;

    protected boolean parsePDU( Object[] pdus, String rStr){

        boolean authIn= false;
        SmsMessage[] msgs;
        msgs = new SmsMessage[pdus.length];
        Log.d(TAG, "202--");
        Toast.makeText(context, "202--", Toast.LENGTH_LONG).show();

        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            msgTel = msgs[i].getOriginatingAddress();

            msgTxt = msgs[i].getMessageBody();
            msgTimeL = msgs[i].getTimestampMillis();
            msgTimeS = Long.toString(msgTimeL);
            timeNow = new java.sql.Timestamp(System.currentTimeMillis());
            timeNowL = (new java.sql.Timestamp(System.currentTimeMillis())).getTime();

            if (msgTxt.toLowerCase().contains(rStr.toLowerCase().trim())) {
                authIn = true;
                Toast.makeText(context, "204--AUTH OK! i=" + i, Toast.LENGTH_LONG).show();
                Log.d(TAG, "204--" + i);
                //TODO отрегистрировать приемник.
                //TODO проверять время отправкии поступления сообщения

                break; //Закончить перебор сообщений
            } else {
                authIn = false;
                Toast.makeText(context, "205--auth NOT ok!!! msg=" + msgTxt + " rStr=" + rStr, Toast.LENGTH_LONG).show();
                Log.d(TAG, "205--" + i);
            }
        }
        return authIn;
    }


    @Override
    public void onReceive(Context ctx, Intent intent) {
        context = ctx;

        Toast.makeText(context, "SmsAuth перехватило смс", Toast.LENGTH_LONG).show();

        SharedPreferences sP = context.getSharedPreferences(context.getString(R.string.PREF_FILE_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();

        Bundle bundle = intent.getExtras();

        Log.d(TAG, "201--");
        Toast.makeText(context, "201--", Toast.LENGTH_LONG).show();

        SmsMessage[] msgs = null;
        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            String rStr = sP.getString(context.getString(R.string.KEY_RND), "");

            msgTel = "";
            msgTxt = "";
            msgTimeL = 0L;
            msgTimeS = "";
            str = "";
            timeNow = new java.sql.Timestamp(System.currentTimeMillis());
            timeNowL = (new java.sql.Timestamp(System.currentTimeMillis())).getTime();

            if (rStr != null && parsePDU(pdus, rStr) == true) {

                    Toast.makeText(context, "003--AUTH OK!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "003--" + msgTxt.toLowerCase() + " : " + context.getString(R.string.AUTH_STR_RAND) + rStr);
                    //TODO отрегистрировать приемник.
                    str = msgTimeS +  "; " + msgTel + "; " + msgTxt;
                    //Изменяем файл, что является сигналом на перехват для слушателя в главной активности
                    editor.putString(context.getString(R.string.KEY_SMS), "Time=" + Long.toString(timeNowL)+ "-" + Long.toString(msgTimeL) + "").apply();
            }else{
                Toast.makeText(context, "auth NOT ok!!! m1=" + msgTxt.length() + " r2=" + rStr.length(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "004--" + msgTxt.toLowerCase() + " : " + context.getString(R.string.AUTH_STR_RAND) + rStr);
            }
        }


    }



}











/*
public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "SmsAuth перехватило смс", Toast.LENGTH_LONG).show();

        SharedPreferences sP = context.getSharedPreferences(context.getString(R.string.PREF_FILE_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();

        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;
        String str = "";
        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            msgs = new SmsMessage[pdus.length];
            String rStr = sP.getString(context.getString(R.string.KEY_RAND), "");

            //parsePDU(pdus, rStr);

            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                msgTel = msgs[i].getOriginatingAddress();

                msgTxt = msgs[i].getMessageBody();
                msgTimeL = msgs[i].getTimestampMillis();
                msgTimeS = Long.toString(msgTimeL);
                timeNow = new java.sql.Timestamp(System.currentTimeMillis());
                timeNowL = (new java.sql.Timestamp(System.currentTimeMillis())).getTime();

                if (msgTxt.toLowerCase().contains(rStr.toLowerCase().trim())) {
                    Toast.makeText(context, "AUTH OK!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "003--" + msgTxt.toLowerCase() + " : " + context.getString(R.string.AUTH_STR_RAND) + rStr);
                    //TODO отрегистрировать приемник.
                    str = msgTimeS +  "; " + msgTel + "; " + msgTxt;
                    editor.putString(context.getString(R.string.KEY_SMS), "Time=" + Long.toString(timeNowL - msgTimeL) + "Передалось, что ли?").apply();

                    break;
                }else{
                    //Toast.makeText(context, "auth NOT ok!!! m1=" + msgTxt.length() + " r2=" + rS.length(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "auth NOT ok!!! m1=" + msgTxt.length() + " r2=" + rStr.length(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "004--" + msgTxt.toLowerCase() + " : " + context.getString(R.string.AUTH_STR_RAND) + rStr);
                }

            }


        }

    }
*/
