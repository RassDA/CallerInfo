package ru.infonum.callerinfo;

/**
 * Created by d1i on 07.04.15.
 * Created by d1i on 11.02.15.
 */

import android.content.BroadcastReceiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

// Использование:   SMSUtils.sendSMS(context, phoneNumber, message);
//                  boolean SMSUtils.canSendSMS(Context context)    проверяет, прописаны ли возможности телефонии в андроиде

// Не используется

public class SmsUtils extends BroadcastReceiver {

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    Toast.makeText(context, context.getString(R.string.sms_send), Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                    Toast.makeText(context, context.getString(R.string.sms_not_send), Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                    Toast.makeText(context, context.getString(R.string.sms_not_send_no_service), Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    Toast.makeText(context, context.getString(R.string.sms_not_send), Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    Toast.makeText(context, context.getString(R.string.sms_not_send_no_radio), Toast.LENGTH_LONG).show();
                    break;
            }
        }
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, context.getString(R.string.sms_receive), Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, context.getString(R.string.sms_not_receive), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    /**
     * Test if device can send SMS
     * @param context
     * @return
     */
    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }





    public static void sendSMS(final Context context, String phoneNumber, String message) {

        if (!canSendSMS(context)) {
            Toast.makeText(context, context.getString(R.string.cannot_send_sms), Toast.LENGTH_LONG).show();
            return;
        }

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), 0);

        final SmsUtils smsUtils = new SmsUtils();
        //register for sending and delivery
        context.registerReceiver(smsUtils, new IntentFilter(SmsUtils.SENT_SMS_ACTION_NAME));
        //context.registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));
        context.registerReceiver(smsUtils, new IntentFilter(SmsUtils.DELIVERED_SMS_ACTION_NAME));

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);

        ArrayList<PendingIntent> sendList = new ArrayList<PendingIntent>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<PendingIntent>();
        deliverList.add(deliveredPI);

        sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);

        //we unsubscribed in 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.unregisterReceiver(smsUtils);
            }
        }, 10000);

    }


    /**
     * Test if device can send SMS
     * @param context
     * @return
     */

    public static boolean canSendSMS2(Context context) {
        //возможно, так нужно вызывать?
        try{
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

        }
        catch(Exception e) {
            // code that doesn't use telephony features
            return true;
        }
    }
/*
    public static void sendSMSBuiltin(final Context context, String phoneNumber, String message) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "Content of the SMS goes here...");
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }
*/
}