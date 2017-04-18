package com.gdgdevfest.demo.network;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.gdgdevfest.demo.Person;
import com.gdgdevfest.demo.Settings;
import com.gdgdevfest.demo.ntlm.NtlmAuthenticator;

import java.io.IOException;
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

        Call<List<Person>> call = service.getBasicNames(5);

        executeCall(call, "basic-data");
    }

    public void getPersonDigestAuth(String userName, String password) {
        final DigestAuthenticator authenticator = new DigestAuthenticator(new Credentials(userName, password));

        httpClient.authenticator(authenticator);

        builder.client(httpClient.build());
        retrofit = builder.build();


        service = retrofit.create(NameWebService.class);
        Call<List<Person>> call = service.getBasicNames(7);

        executeCall(call, "digest-data");
    }


    public void getPersonNtlmAuth(final String userName, final String password, final String domain) {

        NtlmAuthenticator authenticator = new NtlmAuthenticator(userName, password, domain);

        httpClient.authenticator(authenticator);

        builder.client(httpClient.build());
        retrofit = builder.build();


        service = retrofit.create(NameWebService.class);
        Call<List<Person>> call = service.getNtlmNames(4);

        executeCall(call, "ntlm-data");
    }

    private void sendResult(ArrayList<Person> data, String action, int result) {

        Intent sendBack = new Intent(AUTH_RESULT);

        sendBack.putExtra("call", action);
        sendBack.putExtra("result", result);
        sendBack.putParcelableArrayListExtra("data", data);

        //Keep the intent local to the application
        LocalBroadcastManager.getInstance(context).sendBroadcast(sendBack);
    }

    public void getPersonHmacAuth(final String signature) {
        HmacInterceptor interceptor = new HmacInterceptor(signature);

        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor);

            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        service = retrofit.create(NameWebService.class);

        Call<List<Person>> call = service.getHmacNames(3);

        executeCall(call, "hmac-data");
    }

    private void executeCall(Call<List<Person>> call, final String intentMessage) {
        call.enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                int statusCode = response.code();

                if(statusCode == 200) {
                    ArrayList<Person> found = (ArrayList<Person>) response.body();

                    sendResult(found, intentMessage, Activity.RESULT_OK);
                } else {

                    String error = null;
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d(this.getClass().getName(), error);

                    sendResult(new ArrayList<Person>(), intentMessage, AUTH_FAILED);
                }
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                // Log error here since request failed
                sendResult(new ArrayList<Person>(), "hmac-data", AUTH_FAILED);
            }
        });
    }
}
