package com.example.madcamp_project_2;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {
    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.249.19.250:7680/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    RetrofitInterface server = retrofit.create(RetrofitInterface.class);
}
