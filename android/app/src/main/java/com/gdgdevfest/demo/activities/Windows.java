package com.gdgdevfest.demo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gdgdevfest.demo.AuthService;
import com.gdgdevfest.demo.R;

public class Windows extends BaseActivity {

    private Button mLogin;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.windows);

        String title = getIntent().getStringExtra("title");

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);

        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(loginListener);

        mPersonList = (ListView) findViewById(R.id.results);
    }

    private View.OnClickListener loginListener = new View.OnClickListener(){
            public void onClick(View v){
                //Get rid of the keyboard
                InputMethodManager inputManager = (InputMethodManager)
                                                  getSystemService(Windows.this.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                                     InputMethodManager.HIDE_NOT_ALWAYS);


                EditText domainField = (EditText) findViewById(R.id.domain);
                EditText userNameField = (EditText) findViewById(R.id.user_name);
                EditText passwordField = (EditText) findViewById(R.id.password);

                Intent intent = new Intent(Windows.this, AuthService.class);
                intent.setAction("windows-auth");
                intent.putExtra("domain", domainField.getText().toString().trim());
                intent.putExtra("username", userNameField.getText().toString().trim());
                intent.putExtra("password", passwordField.getText().toString().trim());
                startService(intent);
            }
    };
}