package ru.apiok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkTokenRequestListener;
import ru.ok.android.sdk.util.OkScope;

/*
 * Created by valery.ozhiganov on 26.12.13.
 */
public class
        LoginActivity extends Activity implements OkTokenRequestListener, View.OnClickListener {
    private String
    //ID приложения
    //Application ID
            APP_ID = "",

    //Публичный ключ приложения
    //Application public key
            APP_PUBLIC_KEY = "",

    //Секретный ключ приложения
    //Application secret key
            APP_SECRET_KEY = "";

    private Odnoklassniki mOdnoklassniki;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    //создаем объект, привязанный к контексту приложения
    //create object which will bound to the application context
        mOdnoklassniki = Odnoklassniki.createInstance(getApplicationContext(), APP_ID, APP_SECRET_KEY, APP_PUBLIC_KEY);

    //определяем callback на операции с получением токена
    //define callback on authorization
        mOdnoklassniki.setTokenRequestListener(this);
    }

    @Override
    public void onDestroy() {
    //удаляем callback
    //remove callback
        mOdnoklassniki.removeTokenRequestListener();

        super.onDestroy();
    }

    //если всё хорошо, пользователь зашел в наше приложение
    //if all right, user logged in our app
    @Override
    public void onSuccess(String token) {
        Log.v("APIOK", "Your token: " + token);
        //переходим к другой активити, где будем вызывать методы
        //change activity for api interaction
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    //если что-то пошло не так, пользователь не зашел в наше приложение
    //if something goes wrong in authorization
    @Override
    public void onError() {
        Log.v("APIOK", "Error");
        Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_LONG).show();
//        Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show();
    }

    //если что-то пошло не так, пользователь не зашел в наше приложение
    //if something goes wrong in authorization
    @Override
    public void onCancel() {
        //нажал назад
        //press back
        //нажать отменить в вебе
        //press cancel
        Log.v("APIOK", "Cancel");
        Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_LONG).show();
//        Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show();
    }

    //метод обработки нажатия пользователя
    //processing user click
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                Log.v("APIOK", "Clicked");

                //вызываем запрос авторизации. После OAuth будет вызван callback, определенный для объекта
                //request authorization
                mOdnoklassniki.requestAuthorization(this, false, OkScope.VALUABLE_ACCESS);

                break;
        }
    }
}
