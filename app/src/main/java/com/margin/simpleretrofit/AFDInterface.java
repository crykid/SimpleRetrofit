package com.margin.afd_annotation.customer_retrofit;


import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDField;
import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDGet;
import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDPost;
import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDQuery;


import okhttp3.Call;
import okhttp3.ResponseBody;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface AFDInterface {

    @AFDPost("/v3/weather/weatherInfo")
    @FormUrlEncoded
    Call postWeather(@AFDField("city") String city, @AFDField("key") String key);


    @AFDGet("/v3/weather/weatherInfo")
    Call getWeather(@AFDQuery("city") String city, @AFDQuery("key") String key);
}
