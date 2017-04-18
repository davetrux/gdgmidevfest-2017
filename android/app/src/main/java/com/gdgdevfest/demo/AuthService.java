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
        else if ("forms-auth".equals(intent.getAction())) {
            getFormsData(intent.getStringExtra("cookie"));
        }
        else if ("windows-auth".equals(intent.getAction())) {
            getWindowsData(intent.getStringExtra("url"), intent.getStringExtra("username"), intent.getStringExtra("password"), intent.getStringExtra("domain"));
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

    /*
     * Windows Auth
     */
    private void getWindowsData(String url, String userName, String password, String domain){
        NtlmHelper http = new NtlmHelper();
        String webResult;
        int result = 2;
        try {
            webResult = http.getHttp(url, userName, password, domain);
            if(!webResult.equalsIgnoreCase("")) {
                result = Activity.RESULT_OK;
            }
        } catch (IOException e) {
            webResult = "";
            Log.d(getClass().getName(), "Exception calling service", e);
        }

        sendResult(webResult, AUTH_RESULT, "windows-data", result);
    }

    /*
     * Forms Auth
     */
    private void getFormsData(String cookie){
        WebHelper http = new WebHelper(this);
        WebResult webResult;
        int result = 2;
        try {
            webResult = http.getPersonJsonForm(cookie);
            if(webResult.getHttpCode() == 200) {
                result = Activity.RESULT_OK;
            }
        } catch (IOException e) {
            webResult = new WebResult();
            Log.d(getClass().getName(), "Exception calling service", e);
        }

        sendResult(webResult.getHttpBody(), AUTH_RESULT, "forms-data", result);
    }

    /*
     * Basic Auth
     */
    private void getBasicData(String user, String password) {
        WebHelper http = new WebHelper(this);
        http.getPersonBasicAuth(user, password);
    }

    /*
     * Place the results into an intent and return it to the caller
     */
    private void sendResult(String data, String name, String action, int result) {

        Intent sendBack = new Intent(name);

        sendBack.putExtra("call", action);
        sendBack.putExtra("result", result);
        sendBack.putExtra("data", data);

        //Keep the intent local to the application
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendBack);
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
