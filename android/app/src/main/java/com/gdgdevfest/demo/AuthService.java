package com.gdgdevfest.demo;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.gdgdevfest.demo.network.*;
import com.gdgdevfest.demo.ntlm.NtlmAuthenticator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AuthService extends IntentService {

    public static final String AUTH_RESULT = "AUTH-RESULT";

    private static final int AUTH_FAILED = 2;
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private Retrofit retrofit;
    private Retrofit.Builder builder;
    private NameWebService service;

    public AuthService() {
        super("AuthService");

        //UXRFPJGwFvvyJI3vFOMIc19r0JNlNSQydEnYRrZI/W4=

        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("*.digitalhpe.com", "sha256/UXRFPJGwFvvyJI3vFOMIc19r0JNlNSQydEnYRrZI/W4=")
                .build();
        httpClient.certificatePinner(certificatePinner).build();

        builder = new Retrofit.Builder()
                .baseUrl(Settings.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
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
        else if ("windows-auth".equals(intent.getAction())) {
            getPersonNtlmAuth(intent.getStringExtra("username"), intent.getStringExtra("password"), intent.getStringExtra("domain"));
        }
        else if ("basic-auth".equals(intent.getAction())){
            getPersonBasicAuth(intent.getStringExtra("username"), intent.getStringExtra("password"));
        }
        else if ("digest-auth".equals(intent.getAction())){
            getPersonDigestAuth(intent.getStringExtra("username"), intent.getStringExtra("password"));
        }
        else if ("hmac-auth".equals(intent.getAction())){
            getPersonHmacAuth(intent.getStringExtra("username"));
        }
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


    public void getPersonHmacAuth(final String userName) {
        HmacAuth helper = new HmacAuth();

        String signature = helper.createHmacString(userName);

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


    private void sendResult(OauthData data, String name, String action, int result) {

        Intent sendBack = new Intent(name);

        sendBack.putExtra("call", action);
        sendBack.putExtra("result", result);
        sendBack.putExtra("data", data);

        //Keep the intent local to the application
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendBack);
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
                Log.d("AuthService", "REST Failure", t);
                sendResult(new ArrayList<Person>(), "hmac-data", AUTH_FAILED);
            }
        });
    }

    private void sendResult(ArrayList<Person> data, String action, int result) {

        Intent sendBack = new Intent(AUTH_RESULT);

        sendBack.putExtra("call", action);
        sendBack.putExtra("result", result);
        sendBack.putParcelableArrayListExtra("data", data);

        //Keep the intent local to the application
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendBack);
    }
}
