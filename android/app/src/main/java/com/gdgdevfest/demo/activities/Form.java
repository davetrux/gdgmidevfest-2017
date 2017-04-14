package com.gdgdevfest.demo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gdgdevfest.demo.AuthService;
import com.gdgdevfest.demo.R;
import com.gdgdevfest.demo.Settings;

public class Form extends BaseActivity {

    private WebView mWeb;
    private Button mCallService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        String title = getIntent().getStringExtra("title");

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);

        mPersonList = (ListView) findViewById(R.id.results);

        mCallService = (Button) findViewById(R.id.callService);
        mCallService.setOnClickListener(doneListener);

        mWeb = (WebView) findViewById(R.id.web_view);

        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

            });

        CookieManager.getInstance().removeSessionCookie();

        mWeb.loadUrl(Settings.FORM_LOGIN);
    }

    private View.OnClickListener doneListener = new View.OnClickListener(){
            public void onClick(View v){
                CookieManager cookieManager = CookieManager.getInstance();
                String url = mWeb.getUrl();
                final String cookie = cookieManager.getCookie(url);

                //Send cookie value via intent
                Intent intent = new Intent(Form.this, AuthService.class);
                intent.setAction("forms-auth");
                intent.putExtra("cookie", cookie);
                intent.putExtra("url", Settings.FORM_URL);
                startService(intent);

                RelativeLayout parent = (RelativeLayout) findViewById(R.id.parentContainer);
                cookieManager.removeAllCookie();
                parent.removeView(mWeb);
            }
    };
}