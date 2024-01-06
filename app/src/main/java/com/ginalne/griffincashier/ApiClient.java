package com.ginalne.griffincashier;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
//    public static final String BASE_URL = "http://192.168.19.175:8000/api/";
    public static final String BASE_URL = "https://buman.ginalne.my.id/api/";

    public static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null)
            retrofit = (new Retrofit.Builder()).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit;
    }
}
