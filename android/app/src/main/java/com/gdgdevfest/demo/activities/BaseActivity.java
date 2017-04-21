package com.gdgdevfest.demo.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.gdgdevfest.demo.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gdgdevfest.demo.AuthService;
import com.gdgdevfest.demo.Person;
import com.gdgdevfest.demo.PersonAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends Activity {

    public static final String APP_TAG = "com.gdgdevfest.demo";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected ListView mPersonList;
    protected ArrayList<Person> mData;

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(AuthService.AUTH_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(onAuthenticate, filter);
    }

    /*
     * The listener that responds to intents sent back from the service
     */
    protected BroadcastReceiver onAuthenticate = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int serviceResult = intent.getIntExtra("code", -1);
            if (serviceResult == Settings.AUTH_OK) {
                mData = intent.getParcelableArrayListExtra("data");

                BindPersonList(context);

            } else {
                Toast.makeText(context, "Rest call failed.", Toast.LENGTH_SHORT).show();
            }

            Log.d("BroadcastReceiver", "onReceive called");
        }
    };

    /*
     * Helper method to put the list of persons into the ListView
     */
    protected void BindPersonList(Context context) {
        PersonAdapter adapter = new PersonAdapter(context, mData);
        mPersonList.setAdapter(adapter);
    }



}