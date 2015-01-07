package com.mobidevday.demo.network;

import android.util.Log;

import com.mobidevday.demo.Settings;
import com.mobidevday.demo.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by trux on 1/6/15.
 */
public class WebHelper {

    private static final String GET = "GET";

    public WebResult getPersonJsonForm(final String cookie) throws IOException {

        return executeHTTP(Settings.FORM_URL, "Cookie", cookie);
    }

    public WebResult getPersonJsonHmac(final String signature, String md5) throws IOException {

        return executeHTTP(Settings.HMAC_URL, "Authorization", signature);
    }

    private WebResult executeHTTP(String url, String headerName, String headerValue) throws IOException {

        OutputStream os = null;
        BufferedReader in = null;
        final WebResult result = new WebResult();

        try {
            final URL networkUrl = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection) networkUrl.openConnection();
            conn.setRequestMethod(GET);
            conn.setRequestProperty(headerName, headerValue);
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
