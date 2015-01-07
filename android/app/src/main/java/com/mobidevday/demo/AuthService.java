package com.mobidevday.demo;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.mobidevday.demo.network.BasicHelper;
import com.mobidevday.demo.network.HmacAuth;
import com.mobidevday.demo.network.NtlmHelper;
import com.mobidevday.demo.network.OauthData;
import com.mobidevday.demo.network.OauthHelper;
import com.mobidevday.demo.network.WebHelper;
import com.mobidevday.demo.network.WebResult;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class AuthService extends IntentService {

    public static final String AUTH_RESULT = "AUTH-RESULT";
    private static final String HMAC_PRIVATE = "4c4a3f0d-3dff-475a-afcc-6ec86fc0b126";

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

    }

    private void getHmacData(String userName) {
        WebHelper http = new WebHelper();
        WebResult webResult;
        int result = -1;
        try {

            String md5 = HmacAuth.createMd5Hash(Settings.HMAC_URL);

            String hmacString = "GET" + md5 + Settings.HMAC_URL;

            String signature = HmacAuth.calculateRFC2104HMAC(hmacString, HMAC_PRIVATE);

            String authorization = "HMAC " + userName + ":" + signature;

            webResult = http.getPersonJsonHmac(authorization, md5);
            if (webResult.getHttpCode() == 200) {
                result = Activity.RESULT_OK;
            }
        } catch (NoSuchAlgorithmException ax) {
            webResult = new WebResult();
            Log.d(getClass().getName(), "Exception generating hash", ax);
        } catch (SignatureException sx) {
            webResult = new WebResult();
            Log.d(getClass().getName(), "Exception generating signature", sx);
        } catch (IOException e) {
            webResult = new WebResult();
            Log.d(getClass().getName(), "Exception calling service", e);
        }

        sendResult(webResult.getHttpBody(), AUTH_RESULT, "hmac-data", result);
    }

    /*
     * oAuth
     */
    private void getOauthData(String userName, String password, OauthData data) {
        OauthHelper http = new OauthHelper();
        OauthData webResult;
        int result = 1;
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
        int result = -1;
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
        WebHelper http = new WebHelper();
        WebResult webResult;
        int result = -1;
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
        BasicHelper http = new BasicHelper();
        String webResult;
        int result = -1;

        try {
            webResult = http.getPersonJson(user, password);
            if(!webResult.equalsIgnoreCase("")) {
                result = Activity.RESULT_OK;
            }
        } catch (IOException e) {
            webResult = "";
            Log.d(getClass().getName(), "Exception calling service", e);
        }

        sendResult(webResult, AUTH_RESULT, "oauth-data", result);
    }

    /*
     * Get Device Token
     */
    private String authenticateGoogle(String accountName) {
        String token = "";
        try {
            Key props = new Key();
            token = GoogleAuthUtil.getToken(this, accountName, props.getGoogleKey(), null);

        } catch (IOException e) {
            Log.d("IO error", e.getMessage());
        } catch (GoogleAuthException ge) {
            Log.d("Google auth error", ge.getMessage());
        } catch (Exception ex) {
            Log.d("error", ex.getMessage());
        }
        return token;
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
