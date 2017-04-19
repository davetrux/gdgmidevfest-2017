package com.gdgdevfest.demo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpsConnection;
import android.util.Log;

import com.google.gson.Gson;
import com.gdgdevfest.demo.Settings;
import com.gdgdevfest.demo.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 1/3/15.
 */
public class OauthHelper {

    private static final String GET = "GET";
    private static final String AUTH_HEADER = "Authorization";

    public static boolean isOnline(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public OauthData getPersonJson(String userName, String password, OauthData info) throws IOException {

        //Login
        long current = TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
        OauthData loginResult;
        WebResult result;
        long lastReturn;

        if(info == null || current > info.getLastResult() + Settings.OAUTH_REFRESH_TIMEOUT) {
            String url = String.format(Settings.OAUTH_LOGIN, userName, password);

            result = executeHTTP(url, GET, null);
            lastReturn = TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);

            if (result.getHttpCode() != 200) {
                throw new IOException("Server Error");
            }

            Gson parser = new Gson();
            loginResult = parser.fromJson(result.getHttpBody(), OauthData.class);
        } else {
            loginResult = info;
            lastReturn = info.getLastResult();
        }

        //Get new token if necessary
        if(current > lastReturn + Settings.OAUTH_TOKEN_TIMEOUT) {
            //Call for a new token using the refresh token
            String url = String.format(Settings.OAUTH_REFRESH, loginResult.getRefreshToken());
            result = executeHTTP(url, GET, null);
            lastReturn = TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);

            if (result.getHttpCode() != 200) {
                throw new IOException("Server Error");
            }

            Gson parser = new Gson();
            loginResult = parser.fromJson(result.getHttpBody(), OauthData.class);
        }

        //Token OK, Fetch data
        String authHeader = String.format("%s %s", loginResult.getTokenType(), loginResult.getAccessToken());

        result = executeHTTP(Settings.OAUTH_URL, GET, authHeader);

        if(result.getHttpCode() != 200) {
            Log.d(BaseActivity.APP_TAG, result.getHttpBody());
            throw new IOException("Server Error");
        }
        loginResult.setLastResult(lastReturn);
        loginResult.setCallResult(result.getHttpBody());

        return loginResult;
    }

    private WebResult executeHTTP(String url, String method, String authHeader) throws IOException {

        OutputStream os = null;
        BufferedReader in = null;
        final WebResult result = new WebResult();

        try {
            final URL networkUrl = new URL(url);
            final HttpsURLConnection conn = (HttpsURLConnection) networkUrl.openConnection();
            conn.setRequestMethod(method);

            if(authHeader != null &&!authHeader.isEmpty()) {
                conn.setRequestProperty(AUTH_HEADER, authHeader);
            }

            final InputStream inputFromServer = conn.getInputStream();

            in = new BufferedReader(new InputStreamReader(inputFromServer));
            String inputLine;
            StringBuffer json = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }

            result.setHttpBody(json.toString());
            result.setHttpCode(conn.getResponseCode());

            return result;

        } catch (Exception ex) {
            Log.d(BaseActivity.APP_TAG, "HTTP error", ex);
            result.setHttpCode(500);
            return result;
        } finally {
            //clean up
            if (in != null) {
                in.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
