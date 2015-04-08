package ru.infonum.callerinfo;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d1i on 08.04.15.
 */
public class httpPost extends AsyncTask<String, String, String> {
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

            //собираем их вместе и посылаем на сервер
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


}

