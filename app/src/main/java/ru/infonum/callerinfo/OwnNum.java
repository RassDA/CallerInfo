package ru.infonum.callerinfo;

import android.content.Context;
import android.content.SharedPreferences;

public class OwnNum {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor ed;

    OwnNum(Context ctx){  //constructor
        this.context=ctx;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.OWN_NUM_FILE), Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();
        //ed.commit();
    }

    public boolean state() {
        String s = sharedPreferences.getString(context.getString(R.string.OWN_NUM), "");
        return  (s != null && !s.equals(""));
    }

    public boolean save(String phone) {
        //пишем только мы, поэтому там может быть только номер в правильном формате или пусто или null
        if (phone == null){
            phone = "";
        }
        ed.putString(context.getString(R.string.OWN_NUM), phone).commit(); // .apply()
        return !phone.equals("");
    }

    public String get() {
        // получает номер из хранилища. инициализирует хранилище, если надо
        String s = sharedPreferences.getString(context.getString(R.string.OWN_NUM), "");
        if(s == null){
            save("");
            s = "";
        }
        return s;
    }

    public String format(String phone) {
        //проверить правильность формата и преобразовать к стандартному виду
        //возвращает номер в правильном формате или пустую строку, если невозможно преобразовать

        String num = "";
        String buf = phone.trim();

        //ищем "+"
        //если находим - берем подстроку, начиная с "+"
        //удаляем все знаки-нецифры, исключая первый "+"
        //проверяем длину оставшейся подстроки
        //если в рамках - ок

        if (phone.contains("+")){
            buf = phone.substring(phone.indexOf("+"));
        }

        for (int i=0; i< buf.length(); i++){
            //оставляем в строке только цифры и первый плюс, если он есть
            if ( Character.isDigit(buf.charAt(i)) || buf.substring(0,0).equals("+") ) {
                num = num.concat(buf.substring(i,i));
            }
        }

        if ( buf.length() < 11 && buf.length() > 15) {
            save("");
            return "";
        }

        //проверить на российскую нумерацию
        if (num.substring(0,0).equals("8") && num.length() == 11){
            num = "+7" + num.substring(1);
        }else{
            save("");
            return "";
        }
        if (num.contains("+7") && num.length() != 12){
            save("");
            return "";
        }
        //про остальные типы номеров ничего не знаю
        return num;
    }

    public  String interact(String s) {
        //просит пользователя ввести свой номер и верифицирует его по установленной методике
        //через запрос на ввод своего номера и т.д.
        String out = format(s);
        save(out);
        return out;
    }

}
