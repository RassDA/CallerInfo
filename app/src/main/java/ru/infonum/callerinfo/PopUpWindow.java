package ru.infonum.callerinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by d1i on 08.04.15.
 */
public class PopUpWindow {
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;

    public static void showWindow(final Context context, final String phone, final String text) {
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
                    //открываем браузер в новой активности по нажатию на кнопку
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

    public static void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

}
