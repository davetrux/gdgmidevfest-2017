package com.gdgdevfest.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.gdgdevfest.demo.activities.Basic;
import com.gdgdevfest.demo.activities.Form;
import com.gdgdevfest.demo.activities.Windows;
import com.gdgdevfest.demo.activities.oAuth;

public class Main extends Activity {

    private Button mFormButton;
    private Button mBasicButton;
    private Button mWindowsButton;
    private Button mDeviceButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mFormButton = (Button) findViewById(R.id.forms);
        mFormButton.setOnClickListener(accountListener);

        mBasicButton = (Button) findViewById(R.id.basic);
        mBasicButton.setOnClickListener(accountListener);

        mWindowsButton = (Button) findViewById(R.id.windows);
        mWindowsButton.setOnClickListener(accountListener);

        mDeviceButton = (Button) findViewById(R.id.device);
        mDeviceButton.setOnClickListener(accountListener);

        Button digestButton = (Button) findViewById(R.id.digest);
        digestButton.setOnClickListener(accountListener);

        Button hmacButton = (Button) findViewById(R.id.hmac);
        hmacButton.setOnClickListener(accountListener);
    }

    private View.OnClickListener accountListener = new View.OnClickListener(){
        public void onClick(View v){

            if(!isOnline(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Not currently online", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = v.getId();
            Intent intent;

            switch (id){
                case R.id.device:
                    intent = new Intent(Main.this, oAuth.class);
                    intent.putExtra("title", "oAuth");
                    startActivity(intent);
                    break;
                case R.id.basic:
                    intent = new Intent(Main.this, Basic.class);
                    intent.putExtra("title", "HTTP Basic");
                    intent.putExtra("action", "basic-auth");
                    startActivity(intent);
                    break;
                case R.id.forms:
                    intent = new Intent(Main.this, Form.class);
                    intent.putExtra("title", "Web Form");
                    startActivity(intent);
                    break;
                case R.id.windows:
                    intent = new Intent(Main.this, Windows.class);
                    intent.putExtra("title", "Windows");
                    startActivity(intent);
                    break;
                case R.id.digest:
                    intent = new Intent(Main.this, Basic.class);
                    intent.putExtra("title", "Digest");
                    intent.putExtra("action", "digest-auth");
                    startActivity(intent);
                    break;
                case R.id.hmac:
                    intent = new Intent(Main.this, Basic.class);
                    intent.putExtra("title", "HMAC");
                    intent.putExtra("action", "hmac-auth");
                    startActivity(intent);
                    break;
            }
        }
    };

    /*
    * Perform an online check
    */
    private boolean isOnline(Context ctx){
        ConnectivityManager manager =  (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
