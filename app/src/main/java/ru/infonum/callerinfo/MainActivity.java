package ru.infonum.callerinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import static ru.infonum.callerinfo.Utils.requestSettings;

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

        scrollViewMain = (ScrollView) findViewById(R.id.scrollViewMain);
        textViewMain = (TextView) findViewById(R.id.textViewMain);

        //загрузить отладочные настройки с сайта
        String paramS = requestSettings();
        textViewMain.setText(paramS);

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
