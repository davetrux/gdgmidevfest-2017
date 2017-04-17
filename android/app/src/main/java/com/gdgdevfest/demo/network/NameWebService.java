package com.gdgdevfest.demo.network;

import com.gdgdevfest.demo.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author trux on 4/17/17
 */

public interface NameWebService {

    @GET("/gdg/basic/api/names/{amount}")
    Call<List<Person>> getBasicNames(@Path("amount") int amount);

    @GET("/gdg/hmac/api/names/{amount}")
    Call<List<Person>> getHmacNames(@Path("amount") int amount);

    @GET("/gdg/oauth/api/names/{amount}")
    Call<List<Person>> getOauthNames(@Path("amount") int amount);
}
