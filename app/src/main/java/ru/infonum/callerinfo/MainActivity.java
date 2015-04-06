package ru.infonum.callerinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    public boolean ownNumVerified = false;
    String phoneNum;
    TextView textViewMain;
    ScrollView scrollViewMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        scrollViewMain = (ScrollView) findViewById(R.id.scrollViewMain);
        textViewMain = (TextView) findViewById(R.id.textViewMain);

        OwnNum ownNum = new OwnNum(this);

        phoneNum = ownNum.get();

        if (!ownNum.state()) {
            //предложить выбор: пройти верификацию сразу или отложить, вдруг само проверится
            phoneNum = ownNum.interact(phoneNum); //запустить процедуру установления своего номера
            phoneNum = ownNum.format(phoneNum); //проверить существование, допустимость и привести

        }

        Intent intent = getIntent();
        String phone = intent.getStringExtra("num");
        String text = intent.getStringExtra("dev");
        if (text != null) textViewMain.setText(phone + "\n" + text);


    }


    @Override
    protected void onResume() {
        super .onResume();



    }





}
