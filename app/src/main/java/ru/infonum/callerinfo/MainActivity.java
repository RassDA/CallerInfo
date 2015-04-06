package ru.infonum.callerinfo;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    public boolean ownNumVerified = false;
    String phoneNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        OwnNum ownNum = new OwnNum(this);

        phoneNum = ownNum.get();

        if (!ownNum.state()) {
            //предложить выбор: пройти верификацию сразу или отложить, вдруг само проверится
            phoneNum = ownNum.interact(phoneNum); //запустить процедуру установления своего номера
            phoneNum = ownNum.format(phoneNum); //проверить существование, допустимость и привести

        }
    }
    @Override
    protected void onResume() {
        super .onResume();


    }





}
