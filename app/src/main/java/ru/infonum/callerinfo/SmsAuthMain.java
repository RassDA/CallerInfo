package ru.infonum.callerinfo;

/**
 * Created by d1i on 07.04.15.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Random;


public class SmsAuthMain extends Activity {

    public static final String TAG = "SmsAuth";

    public static Timestamp sendingTime;
    public static String sendingTimeS;
    public static long sendingTimeL;
    public static String msgTxt ="";
    public static String msgTel = "";
    public static String randS;
    public static String str;
    public static final int MAXRND = 4;

//Нет смысла возвращать управление в основную активность

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_auth_main);

        final EditText viewTel = (EditText) findViewById(R.id.editTextPhoneNo);
        final EditText viewTxt = (EditText) findViewById(R.id.editTextSMS);
        final TextView viewLog = (TextView) findViewById(R.id.textView3);
        Button btnSendSms = (Button) findViewById(R.id.btnSendSms);

        final SharedPreferences spSms = getSharedPreferences( getString(R.string.PREF_FILE_NAME), MODE_PRIVATE);
        final SharedPreferences.Editor editor = spSms.edit();

        Log.d(TAG, "101-- Time=" + new java.sql.Timestamp(System.currentTimeMillis()).toString());

        // все это инициализация для первого запуска
        randS = getRnd(MAXRND); // для первого раза генерируем здесь
        msgTxt = getString(R.string.AUTH_STR_RAND) + randS; // подготавливаем строку для текста смс из url и случ числа

        viewTxt.setText(msgTxt); // выводим строку в поле текста смс

        editor.putString(getString(R.string.KEY_RND), msgTxt).commit();//apply(); //записываем всю строку полностью в файл
        editor.putString(getString(R.string.KEY_SMS), "--Init").commit();//apply(); // записываем какой-нибудь начальный текст, чтобы были видны его  изменения

        Log.d(TAG, "015-- Time=" + new java.sql.Timestamp(System.currentTimeMillis()).toString());
        Log.d(TAG, "016-- msgTxt=" + msgTxt);

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //textViewLog.setText("001-- Time=" + new java.sql.Timestamp(System.currentTimeMillis()).toString()
                //        + ": Попытка отправить смс через это приложение\n " + msgTxt );

                msgTel = viewTel.getText().toString(); // читаем номер из поля. В нем всегда текст следующего запроса.
                msgTxt = viewTxt.getText().toString(); // и текст смс из поля - он содержит случайное число.

                //новое случайное число записывается в файл только после подтверждения от сервиса, а в поле новое случайное записывается сразу после отправки


                CheckBox checkbox = (CheckBox)findViewById(R.id.checkBox);
                if (checkbox.isChecked()){
                    sendSmsBI(); //если галочка есть - вызываем смс-приложение по-умолчанию
                } else{
                    sendSMS();   //если галочки нет - отправляем смс своим приложением
                }
                //viewLog.setText("Очищено"); //для контроля



                SharedPreferences.OnSharedPreferenceChangeListener listener =
                        new SharedPreferences.OnSharedPreferenceChangeListener() {
                            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                                // listener implementation
                                // Здесь все происходит после получения ответа от сервиса
                                // предполагаем, что ключ правильный

                                editor.putString(getString(R.string.KEY_RND), msgTxt).commit(); //apply(); //записываем новое случайное для следующего запроса                        //записываем то значение, которое в данный момент (после получения ответной смс) выведено в поле
                                str = spSms.getString(key, ""); // прочитаем новое значение ключа, записанное сервисом

                                Toast.makeText(getApplication().getBaseContext(), "014--Ответ=" + str, Toast.LENGTH_LONG).show();
                                Log.d(TAG, "014-- Ответ=" + str);
                                String sBuf = viewLog.getText().toString();
                                viewLog.setText(sBuf + "\n" + "014-- Ответ=" + str);
                            }
                        };


                listener.onSharedPreferenceChanged(spSms, getString(R.string.KEY_SMS));

                randS = getRnd(MAXRND);
                msgTxt = getString(R.string.AUTH_STR_RAND) + randS;

                viewTxt.setText(msgTxt);
                //viewLog.setText("022-- msgTxt=" + msgTxt);
            }
        });

    }


    protected void sendSMS() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(msgTel, null, msgTxt, null, null);
            Toast.makeText(getApplicationContext(), "SmsAuth: 001--Смс отправлено приложением",
                    Toast.LENGTH_LONG).show();
            sendingTime = new java.sql.Timestamp(System.currentTimeMillis());
            sendingTimeL = sendingTime.getTime();
            Log.d(TAG, "001-- Time=" + new java.sql.Timestamp(System.currentTimeMillis()).toString());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SmsAuth: Сбой в отправке смс. Tel= " + msgTel + "Txt=" + msgTxt, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected void sendSmsBI() { //Send Sms by Built-in sms manager
        //msgTel = editTextTel.getText().toString();
        //msgTxt = editTextTxt.getText().toString();

        //Uri smsUri = Uri.parse("smsto:" + msgTel);
        //TODO прочитать предполагаемый номер телефона, проверить формат, подставить смс-менеджеру
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + msgTel));

        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:+79119148047")); // не выводит номер
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType(getString(R.string.PDU_TYPE_F_NAME)); // prompts only sms-mms clients
        intent.putExtra(getString(R.string.PDU_BODY_F_NAME), msgTxt);
        intent.putExtra(getString(R.string.PDU_ADDRESS_F_NAME), getString(R.string.TESTTEL));
        try{
            if (intent.resolveActivity(getPackageManager()) != null) { //тренируемся проверять возможность отправлять смс

                startActivity(intent);

            } else {
                Toast.makeText(getApplication().getBaseContext(), "Смс не поддерживаются. Попробуйте другие способы аутентификации...",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplication().getBaseContext(), "Ошибка. Смс не отправлено...",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    protected String getRnd(int n){
        final Random random = new Random();
        String r = "";
        for(int i = 0; i < n; i++){
            r += String.valueOf(random.nextInt(9)+1); //TODO использовать char
        }
        return r;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }



}


/*
    public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // listener implementation
            final TextView viewLog = (TextView) findViewById(R.id.textView3);

            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.KEY_RND), msgTxt).apply(); //старый ключ отыграл, записываем новый на будущее

            String s = sharedPreferences.getString(key, "");

            Toast.makeText(MainActivity.this, "0111--Ответ=" + s, Toast.LENGTH_LONG).show();
            Log.d(TAG, "0111-- Ответ=" + s);
            String sBuf = viewLog.getText().toString();
            viewLog.setText(sBuf + "\n"+ "0111--Ответ=" + s);
        }
    }
*/