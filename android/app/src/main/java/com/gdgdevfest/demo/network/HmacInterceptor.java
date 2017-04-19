package com.gdgdevfest.demo.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author trux
 * @since 4/17/17
 */

public class HmacInterceptor implements Interceptor {

    private String token;

    public HmacInterceptor(String token) {
        this.token = "HMAC " + token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", token).build();
        return chain.proceed(authenticatedRequest);
    }
}
