package com.mobidevday.demo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.mobidevday.demo.Settings;
import com.mobidevday.demo.activities.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by david on 1/3/15.
 */
public class OauthHelper {

    private static final String GET = "GET";
    private static final String AUTH_HEADER = "Authorization";

    private OauthLoginResult mLoginResult;
    private long mLastReturn;

    public static boolean isOnline(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public String getPersonJson(String userName, String password) throws IOException {

        //Login

        String url= String.format(Settings.OAUTH_LOGIN, userName, password);

        WebResult result = executeHTTP(url, GET, null, null);

        mLastReturn = System.nanoTime();

        if(result.getHttpCode() != 200) {
            throw new IOException("Server Error");
        }

        Gson parser = new Gson();
        mLoginResult = parser.fromJson(result.getHttpBody(), OauthLoginResult.class);

        //Get token if necessary
        if(System.nanoTime() > mLastReturn + Settings.OAUTH_TOKEN_TIMEOUT) {
            //Call for a new token using the refresh token

        }

        //Token OK, Fetch data
        String authHeader = String.format("%s %s", mLoginResult.getTokenType(), mLoginResult.getAccessToken());

        result = executeHTTP(Settings.OAUTH_URL, GET, null, authHeader);

        if(result.getHttpCode() != 200) {
            throw new IOException("Server Error");
        }

        return result.getHttpBody();
    }

    private WebResult executeHTTP(String url, String method, String input, String authHeader) throws IOException {

        OutputStream os = null;
        BufferedReader in = null;
        final WebResult result = new WebResult();

        try {
            final URL networkUrl = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection) networkUrl.openConnection();
            conn.setRequestMethod(method);

            if(authHeader != null &&!authHeader.isEmpty()) {
                conn.setRequestProperty(AUTH_HEADER, authHeader);
            }

            if (input !=null && !input.isEmpty()) {
                //Create HTTP Headers for the content length and type
                conn.setFixedLengthStreamingMode(input.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json");
                //Place the input data into the connection
                conn.setDoOutput(true);
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(input.getBytes());
                //clean up
                os.flush();
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
