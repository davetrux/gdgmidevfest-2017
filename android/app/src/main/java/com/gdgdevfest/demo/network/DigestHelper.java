package com.gdgdevfest.demo.network;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.gdgdevfest.demo.Settings;
import com.gdgdevfest.demo.activities.BaseActivity;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by david on 1/3/15.
 * Helper class for Basic Authentication
 */
public class DigestHelper {

    private static final String GET = "GET";

    public String getPersonJson(final String userName, final String password) throws IOException {

        WebResult result = executeHTTP(Settings.DIGEST_URL, userName, password);

        return result.getHttpBody();
    }

    private WebResult executeHTTP(String url, String userName, String password) throws IOException {

        BufferedReader in = null;
        final WebResult result = new WebResult();
        result.setHttpBody("");
        try {

            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("test user agent");

            URL urlObj = new URL(url);
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);

            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(scope, creds);
            HttpContext credContext = new BasicHttpContext();
            credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

            HttpGet job = new HttpGet(url);
            HttpResponse response = httpClient.execute(host,job,credContext);
            StatusLine status = response.getStatusLine();
            Log.d(BaseActivity.APP_TAG, status.toString());
            httpClient.close();

            result.setHttpCode(status.getStatusCode());

            if(result.getHttpCode() == 200) {
                //Get the data from the body of the response
                InputStream stream = response.getEntity().getContent();
                byte byteArray[] = IOUtils.toByteArray(stream);
                String json = new String(byteArray);
                stream.close();

                result.setHttpBody(json);
            }

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
        }
    }
}
