package ru.infonum.callerinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static ru.infonum.callerinfo.Storage.getResStrValByName;
import static ru.infonum.callerinfo.Storage.setMyContext;
import static ru.infonum.callerinfo.Utils.requestAppSettings;
import static ru.infonum.callerinfo.Utils.toJsonObj;

public class MainActivity extends Activity {

    public boolean ownNumVerified = false;
    String phoneNum;
    TextView textViewMain;
    ScrollView scrollViewMain;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setMyContext(getBaseContext()); //передаю контекст в Storage() -- а как еще?

        scrollViewMain = (ScrollView) findViewById(R.id.scrollViewMain);
        textViewMain = (TextView) findViewById(R.id.textViewMain);

        //загрузить отладочные настройки с сайта
        String response = requestAppSettings();

        JSONObject js = toJsonObj(response);
        try {
            response = js.get("1").toString() + "/" +js.get("2").toString();
        } catch (JSONException e) {
            Log.debug("\nError parsing data " + e.toString());
        }


        textViewMain.setText(response + " | " + getResStrValByName( "SITE_URL", true) );


/*

        final OwnNum ownNum = new OwnNum(this);
        phoneNum = ownNum.get(); //получить свой сохраненный номер или инициализировать хранилище

        if (!ownNum.state()) {
            //предложить выбор: пройти верификацию сразу или отложить - вдруг само проверится
            phoneNum = getString(R.string.TESTTEL);
            phoneNum = ownNum.interact(phoneNum); //запустить процедуру установления своего номера
            //phoneNum = ownNum.formatSave(phoneNum); //проверить существование, допустимость и привести

        }

        if (!getString(R.string.DEBUG_show_in_main).equals("false")) {//вместо всплывающего окна - вывод на главную
            Intent intent = getIntent();
            String phone = intent.getStringExtra(getString(R.string.NUM_I));
            String text = intent.getStringExtra(getString(R.string.DEV_I));
            if (text != null) textViewMain.setText(phone + "\n" + text); //показать принятое ресивером
        }
*/
    }



}
