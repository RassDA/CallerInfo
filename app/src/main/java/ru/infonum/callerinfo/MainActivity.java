package ru.infonum.callerinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static ru.infonum.callerinfo.Storage.*;
import static ru.infonum.callerinfo.Utils.jsonObject;
import static ru.infonum.callerinfo.Utils.requestAppSettings;
import static ru.infonum.callerinfo.Utils.selectJsonLastObj;
import static ru.infonum.callerinfo.Utils.strToNewJsonObj;

public class MainActivity extends Activity {

    public boolean ownNumVerified = false;
    String phoneNum;
    TextView textViewMain;
    EditText editText;
    ScrollView scrollViewMain;
    Button buttonSend;
    JSONObject jj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext(); //передаю контекст в Storage() -- а как еще?

        //initRes();

        scrollViewMain = (ScrollView) findViewById(R.id.scrollViewMain);
        textViewMain = (TextView) findViewById(R.id.textViewMain);
        editText = (EditText) findViewById(R.id.editText);

        String s = "";
        s = selectJsonLastObj ( requestAppSettings() );
        st = s;//------------------------------------------

        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            Log.debug("\nError parsing data " + e.toString());
        }


        final OwnNum ownNum = new OwnNum(this);
        phoneNum = ownNum.get(); //получить свой сохраненный номер или инициализировать хранилище

        if (!ownNum.state()) {
            //предложить выбор: пройти верификацию сразу или отложить - вдруг само проверится
            phoneNum = getByName("TESTTEL");

            phoneNum = ownNum.interact(phoneNum); //запустить процедуру установления своего номера
            //phoneNum = ownNum.formatSave(phoneNum); //проверить существование, допустимость и привести

        }

        if (!getString(R.string.DEBUG_show_in_main).equals("false")) {//вместо всплывающего окна - вывод на главную
            Intent intent = getIntent();
            String phone = intent.getStringExtra(getString(R.string.NUM_I));
            String text = intent.getStringExtra(getString(R.string.DEV_I));
            if (text != null) textViewMain.setText(phone + "\n" + text); //показать принятое ресивером
        }

        textViewMain.setText(st);


    }

    public void btnSendClick(View view){

        String s = "";
        String s2 = "";
        s = editText.getText().toString();
        String s1 = "";

        try {
            jsonObject = new JSONObject(s);
            s1 = jsonObject.toString();

            try {
                s2 = jsonObject.get("1").toString();
            } catch (JSONException e) {
                Log.debug("\nError parsing data " + e.toString());
                s2 = "Ошибка получения элемента";
            }

        } catch (JSONException e) {
            Log.debug("\nError parsing data " + e.toString());
            s1 = "Ошибка получения объекта";
        }


        textViewMain.setText(s1 + " # " + s2);
    }

}
