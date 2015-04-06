package ru.infonum.callerinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


//TODO сделать локальное хранилище для собственного номера
//TODO вынести обработку из ресивера в запускаемый сервис
//TODO при исходящем звонке записывать исходящий номер на страницу своего номера с тегом
//TODO при входящем вызове пытаться прочитать свой номер со страницы входящего номера
//TODO сохранять свой номер в хранилище
//TODO аналогичная процедура для определения своего номера при операциях с смс
//TODO


public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;
    Context context;
    JSONObject json;
    SharedPreferences sharedPreferences;

    final boolean DBG_show_in_main = false;


    @Override
    public void onReceive(Context ctx, Intent intent) { //распознан входящий вызов
        context = ctx; // попользуемся в другом классе Тостами :)

        // при входящем звонке мы можем определить свой номер:
        // - прочитав его со страницы номера или
        // - запросив его у звонящего приложения
        // -
        OwnNum ownNum = new OwnNum(context);


        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            // обнаружено какое-то изменение состояния
/*
            Bundle bundle = intent.getExtras();
            if(null == bundle)
                return;
            //ловим исходящий номер
            String outPhone = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            phoneNumber = ownNum.format(outPhone);
            if (ownNum.state()){
                //saveTelNum(context.getString(OWNNUM));
            }
*/
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //обнаружен входящий звонок

                try {//Грязноватый хак, рекомендуемый многими примерами в сети, но не обязательный
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    //ну и ладно
                }


                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                Log.debug("Show window: " + phoneNumber);


                // лезем на страницу вызывающего телефона и получаем все его данные, какие есть
                String text = postString(context.getString(R.string.SITE_URL) + context.getString(R.string.SITE_API_READ), "", phoneNumber.trim());


                //подбираем информацию для окна из полученной строки

                //  преобразуем в json, чтобы проще искать

                //String s = text.substring(text.lastIndexOf("{"));

                //try {
                //    json = new JSONObject(text);
                //} catch (JSONException e) {
                //    e.printStackTrace();
                //} catch (NullPointerException e) {
                //    e.printStackTrace();
                //}
                //JSONArray jarr = text.toJSONArray();


                if (context.getString(R.string.DBG_show_in_main).equals("false")) {
                    //показываем во всплывающем окне
                    showWindow(context, phoneNumber, "\n" + text.substring(text.lastIndexOf("{"))
                            .replace("\\" + "\"", "\"").replace("\\" + "\\" + "\\", "")
                            .replace(",", "\n")); //чудеса, если использовать локальную переменную

                } else {
                    //переносим вывод на экран главной активности
                    Intent intentMain = new Intent();
                    intentMain.setClass(context, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //обязательный
                            .putExtra(context.getString(R.string.NUM_I), phoneNumber)
                            .putExtra(context.getString(R.string.DEV_I), text);
                    context.startActivity(intentMain);
                }


            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера / разговор) - закрываем окно, что бы не мешать
                if (incomingCall) {
                    Log.debug("Close window.");
                    closeWindow();
                    incomingCall = false;
                    //отправить данные телефона на сайт номера, если свой номер телефона определен

                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок"
                if (incomingCall) {
                    Log.debug("Close window.");
                    closeWindow();
                    incomingCall = false;
                }
            }
        }
    }

    private void showWindow(final Context context, final String phone, final String text) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_VERTICAL;

        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);

        TextView textView=(TextView) windowLayout.findViewById(R.id.textView);
        Button buttonClose = (Button) windowLayout.findViewById(R.id.buttonClose);
        Button button = (Button) windowLayout.findViewById(R.id.button);
        //button.setText(phone + text);

        textView.setText(phone + "\n" + text);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phone.length() > 1) {
                    //работает только этот вариант
                    //открываем в браузере в новом окне по нажатию на кнопку
                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(context.getString(R.string.SITE_URL) +
                                    context.getString(R.string.SITE_NUMPAGE) + phone.replace("+", "")));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    context.startActivity(intent);
                }

            }
        });

        windowManager.addView(windowLayout, params);
    }

    private void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }



    public String postString(String url, String param1, String param2) {

        String response = "";

        PostTask postTask = new PostTask();
        postTask.execute(url, param1, param2); // post-запрос по этому адресу и этими пост-параметрами

        try {
            response = postTask.get();
            if (response == null) response = "";


        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "[1]=" + param1 + " [2]=" + param2, Toast.LENGTH_LONG).show();
        return response;

    }


}


class PostTask extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {

        try {
            //создаем запрос на сервер
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler<String> res = new BasicResponseHandler();
            //он у нас будет посылать post запрос
            //HttpPost postMethod = new HttpPost("http://checkin.infonum.ru/api/read.php");
            HttpPost postMethod = new HttpPost(params[0]);

            //будем передавать два параметра
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("text", params[1]));
            //nameValuePairs.add(new BasicNameValuePair("phone", "+79500069461"));
            nameValuePairs.add(new BasicNameValuePair("phone", params[2]));

            //собераем их вместе и посылаем на сервер
            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //получаем ответ от сервера
            HttpResponse response = hc.execute(postMethod);
            String resp = EntityUtils.toString(response.getEntity());
            return resp;

        } catch (Exception e) {
            System.out.println("Exp=" + e);
        }
        return null;
    }

//    @Override
//    protected void onPostExecute(Void result) {
//        super.onPostExecute(result);
//        showWindow(context, params[1], "+" + params[2]);
//    }



}
/*

// http://www.pvsm.ru/android/22342

String phoneNumber = "";
public class CallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            //получаем исходящий номер
            phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //телефон звонит, получаем входящий номер
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //телефон находится в режиме звонка (набор номера / разговор)
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //телефон находиться в ждущем режиме. Это событие наступает по окончанию разговора, когда мы уже знаем номер и факт звонка
            }
        }
    }
}



private static final String CONTENT_SMS = "content://sms/";
private static long id = 0;
//регистрируем обработчик изменений контента сообщений
ContentResolver contentResolver = getBaseContext().getContentResolver();
contentResolver.registerContentObserver(Uri.parse(CONTENT_SMS),true, new OutgoingSmsObserver(new Handler()));
private class OutgoingSmsObserver extends ContentObserver {
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Uri uriSMSURI = Uri.parse(CONTENT_SMS);
        Cursor cur = getContentResolver().query(uriSMSURI, null, null,null, null);
        cur.moveToNext();
        String protocol = cur.getString(cur.getColumnIndex("protocol"));
        if(protocol == null){
            long messageId = cur.getLong(cur.getColumnIndex("_id"));
            //проверяем не обрабатывали ли мы это сообщение только-что
            if (messageId != id){
                id = messageId;
                int threadId = cur.getInt(cur.getColumnIndex("thread_id"));
                Cursor c = getContentResolver().query(Uri.parse("content://sms/outbox/" + threadId), null, null, null, null);
                c.moveToNext();
                //получаем адрес получателя
                String address = cur.getString(cur.getColumnIndex("address"));
                //получаем текст сообщения
                String body= cur.getString(cur.getColumnIndex("body"));
                //делаем что-то с сообщением
            }
        }
    }
}

public class OutgoingCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();

                if(null == bundle)
                        return;

                String phonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                Log.i("OutgoingCallReceiver",phonenumber);
                Log.i("OutgoingCallReceiver",bundle.toString());

                String info = "Detect Calls sample application\nOutgoing number: " + phonenumber;

                Toast.makeText(context, info, Toast.LENGTH_LONG).show();
        }
}




*/

