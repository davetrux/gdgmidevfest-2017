package com.gdgdevfest.demo.network;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gdgdevfest.demo.Person;
import com.gdgdevfest.demo.Settings;
import com.gdgdevfest.demo.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author trux on 1/6/15
 */
public class WebHelper {

    public static final String AUTH_RESULT = "AUTH-RESULT";
    private static final String GET = "GET";
    private static final int AUTH_FAILED = 2;
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private Retrofit retrofit;
    private Retrofit.Builder builder;
    private NameWebService service;
    private IntentService context;

    public WebHelper(IntentService service) {
        builder = new Retrofit.Builder()
                .baseUrl(Settings.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        //retrofit = builder.build();
        context = service;

    }

    public void getPersonBasicAuth(String userName, String password) {

        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(userName, password);

        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor);

            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        service = retrofit.create(NameWebService.class);

        final WebResult result = new WebResult();

        Call<List<Person>> call = service.getBasicNames(5);

        call.enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                int statusCode = response.code();
                ArrayList<Person> found = (ArrayList<Person>) response.body();

                sendResult(found, "basic-data", Activity.RESULT_OK);
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                // Log error here since request failed

                sendResult(new ArrayList<Person>(), "basic-data", AUTH_FAILED);
            }
        });
    }


    private void sendResult(ArrayList<Person> data, String action, int result) {

        Intent sendBack = new Intent(AUTH_RESULT);

        sendBack.putExtra("call", action);
        sendBack.putExtra("result", result);
        sendBack.putParcelableArrayListExtra("data", data);

        //Keep the intent local to the application
        LocalBroadcastManager.getInstance(context).sendBroadcast(sendBack);
    }

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
