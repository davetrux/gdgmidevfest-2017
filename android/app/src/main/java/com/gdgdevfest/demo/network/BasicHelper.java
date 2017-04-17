package com.gdgdevfest.demo.network;

import android.util.Log;

import com.gdgdevfest.demo.Settings;
import com.gdgdevfest.demo.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by david on 1/3/15.
 * Helper class for Basic Authentication
 */
public class BasicHelper {

    private static final String GET = "GET";
    private static int mRetries;

    public String getPersonJson(final String userName, final String password) throws IOException {

        mRetries = 0;

        //This is the key action for HTTP Basic
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                if(mRetries > 0) {
                    throw new SecurityException("Unauthorized");
                }

                mRetries ++;

                return new PasswordAuthentication(userName, password.toCharArray());
            }
        });

        WebResult result = executeHTTP(Settings.BASIC_URL);

        return result.getHttpBody();
    }

    private WebResult executeHTTP(String url) throws IOException {

        BufferedReader in = null;
        final WebResult result = new WebResult();
        result.setHttpBody("");
        final URL networkUrl = new URL(url);
        final HttpsURLConnection conn = (HttpsURLConnection) networkUrl.openConnection();

        try {

            conn.setRequestMethod(GET);

            final InputStream inputFromServer = conn.getInputStream();

            in = new BufferedReader(new InputStreamReader(inputFromServer));
            String inputLine;
            StringBuilder json = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }

            result.setHttpBody(json.toString());
            result.setHttpCode(conn.getResponseCode());

            return result;

        } catch (SecurityException sx) {
            Log.d(BaseActivity.APP_TAG, "Authentication error", sx);
            int status = conn.getResponseCode();
            result.setHttpCode(status);
            return result;
        } catch (Exception ex) {
            Log.d(BaseActivity.APP_TAG, "HTTP error", ex);
            int status = conn.getResponseCode();
            result.setHttpCode(status);
            return result;
        } finally {
            //clean up
            if (in != null) {
                in.close();
            }
        }
    }
}
