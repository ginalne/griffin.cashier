package com.ginalne.griffincashier;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("v1/bill/add")
    @Headers({"Accept: application/json"})
    Call<ApiResponse.Bill> billAdd(@FieldMap Map<String, String> map);

    @POST("v1/product/header:list?all=true")
    Call<ApiResponse.ProductHeader> productlist();
}
