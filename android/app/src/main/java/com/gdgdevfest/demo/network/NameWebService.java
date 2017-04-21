package com.gdgdevfest.demo.network;

import com.gdgdevfest.demo.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author trux
 * @since 4/17/17
 */

public interface NameWebService {

    @GET("/basic/api/names/{amount}")
    Call<List<Person>> getBasicNames(@Path("amount") int amount);

    @GET("/hmac/api/names/{amount}")
    Call<List<Person>> getHmacNames(@Path("amount") int amount);

    @GET("/gdg/api/names/{amount}")
    Call<List<Person>> getOauthNames(@Path("amount") int amount);

    @GET("/gdg/oauth/token?grant_type=password&client_id=codemash-client&username={user}&password={pass}")
    Call<List<Person>> loginOauth(@Path("user") String user, @Path("pass") String pass);

    @GET("/gdg/oauth/token?client_id=codemash-client&grant_type=refresh_token&refresh_token={token}")
    Call<List<Person>> refreshOauth(@Path("user") String token);

    @GET("https://gdg.digitalhpe.com/api/names/{amount}")
    Call<List<Person>> getNtlmNames(@Path("amount") int amount);
}
