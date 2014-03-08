package ru.apiok;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ok.android.sdk.Odnoklassniki;

/*
 * Created by valery.ozhiganov on 26.12.13.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private Odnoklassniki mOdnoklassniki;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //получаем объект, созданный в предыдущей активити
        //get object, created in previous activity
        mOdnoklassniki = Odnoklassniki.getInstance(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loadFriends:
                //инициируем получение списка наших друзей
                //start loading friend's info (id)
                new LoadFriends().execute();
                break;
        }
    }

    //класс для вызова метода friends.get
    //методы api должны вызываться не из ui потока
    //class for method friends.get
    //api methods must called from not UI thread
    private class LoadFriends extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                //непосредственный вызов метода api
                //calling api method
                return mOdnoklassniki.request("friends.get", null, "get");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.v("APIOK", "Response on friends.get: " + result);

                //переводим строку в вид, необходимый для следующего запроса
                //convert string for next request
                String friendsId = jsonArrayToString(result);

                //инициируем получение информации о наших друзьях
                //start loading friend's info (first and last name)
                new LoadFriendsInfo().execute(friendsId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //метод, преобразующий строку вида ["AAA", "BBB", "CCC"] в AAA,BBB,CCC
        //method which convert string from ["AAA", "BBB", "CCC"] to AAA,BBB,CCC
        private String jsonArrayToString(String str) throws JSONException {
            JSONArray array = new JSONArray(str);
            StringBuilder builder = new StringBuilder();

            for (int keep = 0; keep < array.length(); keep++) {
                String element = array.getString(keep);
                builder.append(',').append(element);
            }

            return builder.substring(1);
        }
    }

    //класс для вызова метода users.getInfo
    //методы api должны вызываться не из ui потока
    //class for method users.getInfo
    //api methods must called from not UI thread
    private class LoadFriendsInfo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String friendsId = params[0];

            // в Map передаем наши GET-параметры
            //в данном случае uids - id наших друзей, fields - необходимые поля
            //put in Map GET-parameters
            //in our case uids - friend's id, fields - requested fields
            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("uids", friendsId);
            requestParams.put("fields", "last_name, first_name");

            try {
                //непосредственный вызов метода api
                //calling api method
                return mOdnoklassniki.request("users.getInfo", requestParams, "get");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("APIOK", "Response on users.getInfo: " + result);

            try {
                List<String> friends = getFriendsInfoFromResponse(result);

                ListView listView = (ListView) MainActivity.this.findViewById(R.id.friendsList);
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, friends));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //метод, преобразующий json-ответ в список записей имя-фамилия друзей
        //method which convert json-array to list with first and last names
        private List<String> getFriendsInfoFromResponse(String response) throws JSONException {
            List<String> friends = new ArrayList<String>();
            JSONArray friendsJSON = new JSONArray(response);

            for (int keep = 0; keep < friendsJSON.length(); keep++) {
                StringBuilder builder = new StringBuilder();
                JSONObject friend = friendsJSON.getJSONObject(keep);

                builder.append(friend.getString("first_name")).append(' ').append(friend.getString("last_name"));
                friends.add(builder.toString());
            }

            return friends;
        }
    }
}