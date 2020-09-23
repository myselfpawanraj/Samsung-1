package com.example.samsungevent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl)
    {
        Gson gson = new GsonBuilder().setLenient().create();
       // OkHttpClient httpClient = new OkHttpClient();


        OkHttpClient client = new OkHttpClient();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}