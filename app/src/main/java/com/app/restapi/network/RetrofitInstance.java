package com.app.restapi.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://rest-api.mobidev-sandbox.com/api/";



    public static Retrofit getRetrofitInstance() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
