package com.example.samsungevent;

import android.app.Application;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ClientAPI {
   // @Headers({"Accept: application/json"})

    @POST("/signin")
   // @FormUrlEncoded
    Call<ResponseClient> login(
            @Body JsonObject object
//           @Header("Content-Type") String content_type,
//            @Field("email") String email,
//            @Field("pass") String password
    );
    //@Headers({"Accept: application/json"})


    @POST("/signup")
    //@FormUrlEncoded
    Call<ResponseClient2> signup (
            @Body JsonObject object
//            @Header("Content-Type") String content_type,
//            @Field("name") String name,
//            @Field("email") String email,
//            @Field("pass")String pass
    );

}
