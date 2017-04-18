package com.gdgdevfest.demo;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.gdgdevfest.demo.network.*;
import java.io.IOException;


public class AuthService extends IntentService {

    public static final String AUTH_RESULT = "AUTH-RESULT";



    public AuthService() {
        super("AuthService");
    }

    /*
     * Decide which web service is being requested
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        if ("oauth-auth".equals(intent.getAction())) {
            OauthData data = null;
            if(intent.hasExtra("oauth-data")){
                data = intent.getParcelableExtra("oauth-data");
            }

            getOauthData(intent.getStringExtra("username"), intent.getStringExtra("password"), data);
        }
        else if ("windows-auth".equals(intent.getAction())) {
            getNtlmData(intent.getStringExtra("username"), intent.getStringExtra("password"), intent.getStringExtra("domain"));
        }
        else if ("basic-auth".equals(intent.getAction())){
            getBasicData(intent.getStringExtra("username"), intent.getStringExtra("password"));
        }
        else if ("digest-auth".equals(intent.getAction())){
            getDigestData(intent.getStringExtra("username"), intent.getStringExtra("password"));
        }
        else if ("hmac-auth".equals(intent.getAction())){
            getHmacData(intent.getStringExtra("username"));
        }
    }

    private void getDigestData(String userName, String password){
        WebHelper http = new WebHelper(this);
        http.getPersonDigestAuth(userName, password);
    }

    private void getHmacData(String userName) {
        WebHelper http = new WebHelper(this);
        HmacAuth helper = new HmacAuth();
        String authorization = helper.createHmacString(userName);

        http.getPersonHmacAuth(authorization);

    }

    /*
     * oAuth
     */
    private void getOauthData(String userName, String password, OauthData data) {
        OauthHelper http = new OauthHelper();
        OauthData webResult;
        int result = 2;
        try {

            webResult = http.getPersonJson(userName, password, data);

            if(!webResult.getCallResult().equalsIgnoreCase("")) {
                result = Activity.RESULT_OK;
            }
        } catch (IOException e) {
            webResult = new OauthData();
            Log.d(getClass().getName(), "Exception calling service", e);
        }

        sendResult(webResult, AUTH_RESULT, "oauth-data", result);
    }

    private void getNtlmData(String userName, String password, String domain){
        WebHelper http = new WebHelper(this);
        http.getPersonNtlmAuth(userName, password, domain);
    }

       /*
     * Basic Auth
     */
    private void getBasicData(String user, String password) {
        WebHelper http = new WebHelper(this);
        http.getPersonBasicAuth(user, password);
    }

    private void sendResult(OauthData data, String name, String action, int result) {

        Intent sendBack = new Intent(name);

        sendBack.putExtra("call", action);
        sendBack.putExtra("result", result);
        sendBack.putExtra("data", data);

        //Keep the intent local to the application
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendBack);
    }

}
