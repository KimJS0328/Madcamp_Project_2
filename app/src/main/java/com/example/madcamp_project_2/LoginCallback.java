package com.example.madcamp_project_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginCallback implements FacebookCallback<LoginResult> {

    private Activity mActivity;

    LoginCallback(Activity activity) {
        mActivity = activity;
    }

    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e("Callback :: ", "onSuccess");
        requestMe(loginResult.getAccessToken());
    }

    // 로그인 창을 닫을 경우, 호출됩니다.
    @Override
    public void onCancel() {
        Log.e("Callback :: ", "onCancel");
    }

    // 로그인 실패 시에 호출됩니다.
    @Override
    public void onError(FacebookException error) {
        Log.e("Callback :: ", "onError : " + error.getMessage());
    }


    // 사용자 정보 요청
    public void requestMe(AccessToken token) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        Log.e("result",object.toString());
                        final RetrofitConnection retrofitConnection = new RetrofitConnection();
                        try {
                            retrofitConnection.server.getUser(object.getString("id"))
                                .enqueue(new Callback<LoginData>() {
                                    @Override
                                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("Server.findUser", "onSuccess");
                                            Intent intent = new Intent(mActivity, MainActivity.class);
                                            intent.putExtra("USER_ID", response.body().getUserId());

                                            mActivity.startActivity(intent);
                                            mActivity.finish();
                                        }
                                        else {
                                            Log.d("Server.findUser", "onSuccess:NotSuccessful: " + response.body());
                                            LoginData user = new LoginData();
                                            user.setUser(object);
                                            retrofitConnection.server.createUser(user).enqueue(new Callback<LoginData>() {
                                                @Override
                                                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                                    if (response.isSuccessful()) {
                                                        Log.d("Server.createUser", "onSuccess");
                                                        Intent intent = new Intent(mActivity, MainActivity.class);
                                                        intent.putExtra("USER_ID", response.body().getUserId());

                                                        mActivity.startActivity(intent);
                                                        mActivity.finish();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<LoginData> call, Throwable t) {
                                                    Log.d("Server.createUser", "onFailure" + t.toString());

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<LoginData> call, Throwable t) {
                                        Log.d("Server.findUser", "onFailure" + t.toString());
                                    }
                                });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                 }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

}