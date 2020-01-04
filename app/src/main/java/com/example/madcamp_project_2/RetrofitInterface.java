package com.example.madcamp_project_2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("login/login/{userId}")
    Call<LoginData> getUser(@Path("userId") String userId);

    @POST("login/login/create")
    Call<LoginData> createUser(@Body LoginData user);
}
