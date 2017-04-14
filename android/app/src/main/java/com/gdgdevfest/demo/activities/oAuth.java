package com.gdgdevfest.demo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gdgdevfest.demo.AuthService;
import com.gdgdevfest.demo.Person;
import com.gdgdevfest.demo.R;
import com.gdgdevfest.demo.network.OauthData;

import java.util.ArrayList;


public class oAuth extends BaseActivity {

    private OauthData mOauthInfo = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic);

        String title = getIntent().getStringExtra("title");

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);

        Button mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(loginListener);

        mPersonList = (ListView) findViewById(R.id.results);
    }

    private View.OnClickListener loginListener = new View.OnClickListener(){
        public void onClick(View v){
            //Hide the keyboard
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            EditText userNameField = (EditText) findViewById(R.id.user_name);
            EditText passwordField = (EditText) findViewById(R.id.password);

            //Send credentials via intent
            Intent intent = new Intent(oAuth.this, AuthService.class);
            intent.setAction("oauth-auth");
            intent.putExtra("username", userNameField.getText().toString().trim());
            intent.putExtra("password", passwordField.getText().toString().trim());

            if(mOauthInfo != null) {

                mOauthInfo.setCallResult("");

                intent.putExtra("oauth-data", mOauthInfo);
            }

            startService(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(AuthService.AUTH_RESULT);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(super.onAuthenticate);
        LocalBroadcastManager.getInstance(this).registerReceiver(onServiceResult, filter);
    }

    /*
    * The listener that responds to intents sent back from the service
    */
    private BroadcastReceiver onServiceResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int serviceResult = intent.getIntExtra("result", -1);
            if (serviceResult == RESULT_OK && intent.getStringExtra("call").equalsIgnoreCase("oauth-data")) {

                mOauthInfo = intent.getParcelableExtra("data");

                String json = mOauthInfo.getCallResult();

                Gson parser = new Gson();
                mData = parser.fromJson(json, new TypeToken<ArrayList<Person>>(){}.getType());

                BindPersonList(context);

            } else {
                Toast.makeText(context, "Rest call failed.", Toast.LENGTH_LONG).show();
            }

            Log.d("BroadcastReceiver", "onReceive called");
        }
    };
}