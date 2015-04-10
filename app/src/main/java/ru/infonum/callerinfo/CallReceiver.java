package ru.infonum.callerinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.json.JSONObject;

import static ru.infonum.callerinfo.PopUpWindow.closeWindow;
import static ru.infonum.callerinfo.PopUpWindow.showWindow;
import static ru.infonum.callerinfo.Utils.postString;
import static ru.infonum.callerinfo.Storage.*;


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
    static Context context;
    JSONObject json;

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
                //String text = postString(getByName("SITE_URL") + getByName("SITE_API_READ"), "", phoneNumber.trim());
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


                if (context.getString(R.string.DEBUG_show_in_main).equals("false")) {
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

