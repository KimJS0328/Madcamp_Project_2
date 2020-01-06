package com.example.madcamp_project_2;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("login/login/{userId}")
    Call<LoginData> getUser(@Path("userId") String userId);

    @POST("login/login/create")
    Call<LoginData> createUser(@Body LoginData user);

    @GET("image/userId/{userId}")
    Call<List<String>> getImgList(@Path("userId") String userId);

    @Multipart
    @POST("image/userId/{userId}")
    Call<String> uploadImage(@Path("userId") String userId, @Part MultipartBody.Part file);

    @GET("image/userId/{userId}/{image}")
    Call<String> deleteImage(@Path("userId") String userId, @Path("image") String image);

    @GET("contact/userId/{userId}")
    Call<List<ContactItem>> getContactList(@Path("userId") String userId);

    @Multipart
    @POST("contact/userId/{userId}/{name}/{phoneNumber}")
    Call<String> createContact(@Path("userId") String userId, @Path("name") String name, @Path("phoneNumber") String phoneNumber, @Part MultipartBody.Part profile);

}
