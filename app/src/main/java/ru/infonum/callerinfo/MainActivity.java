package ru.infonum.callerinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

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

        final OwnNum ownNum = new OwnNum(this);

        phoneNum = ownNum.get();

        if (!ownNum.state()) {
            //предложить выбор: пройти верификацию сразу или отложить, вдруг само проверится
            phoneNum = ownNum.interact(phoneNum); //запустить процедуру установления своего номера
            phoneNum = ownNum.format(phoneNum); //проверить существование, допустимость и привести

        }

        if (!getString(R.string.DBG_show_in_main).equals("false")) {//вместо всплывающего окна - на главной
            Intent intent = getIntent();
            String phone = intent.getStringExtra(getString(R.string.NUM_I));
            String text = intent.getStringExtra(getString(R.string.DEV_I));
            if (text != null) textViewMain.setText(phone + "\n" + text);
        }

        button = (Button) findViewById(R.id.button);
        View.OnClickListener oclButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = ownNum.interact(phoneNum); //запустить процедуру установления своего номера
            }
        };
        button.setOnClickListener(oclButton);


    }

    @Override
    protected void onResume() {
        super .onResume();



    }





}
