package com.mobidevday.demo.network;

import android.util.Log;

import com.mobidevday.demo.Settings;
import com.mobidevday.demo.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by david on 1/3/15.
 */
public class BasicHelper {

    private static final String GET = "GET";
    private static int mRetries = 0;

    public String getPersonJson(final String userName, final String password) throws IOException {

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

        OutputStream os = null;
        BufferedReader in = null;
        final WebResult result = new WebResult();
        result.setHttpBody("");
        try {
            final URL networkUrl = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection) networkUrl.openConnection();
            conn.setRequestMethod(GET);

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

        } catch (SecurityException sx) {
            Log.d(BaseActivity.APP_TAG, "Authentication error", sx);
            result.setHttpCode(401);
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
