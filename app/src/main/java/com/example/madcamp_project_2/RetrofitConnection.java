package com.example.madcamp_project_2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.249.19.250:7680/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    RetrofitInterface server = retrofit.create(RetrofitInterface.class);
}
