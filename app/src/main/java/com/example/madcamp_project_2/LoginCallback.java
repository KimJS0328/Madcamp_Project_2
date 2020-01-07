package com.example.madcamp_project_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
                            SharedPreferences pref = mActivity.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("id", object.getString("id"));
                            editor.putString("name", object.getString("name"));
                            editor.commit();

                            final LoginData data = new LoginData();
                            data.setPasswd("");
                            data.setUserId(object.getString("id"));
                            data.setName(object.getString("name"));

                            retrofitConnection.server.getUser(data)
                                .enqueue(new Callback<LoginData>() {
                                    @Override
                                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("Server.findUser", "onSuccess");
                                            Intent intent = new Intent(mActivity, MainActivity.class);

                                            mActivity.startActivity(intent);
                                            mActivity.finish();
                                        }
                                        else {
                                            Log.d("Server.findUser", "onSuccess:NotSuccessful: " + response.body());
                                            retrofitConnection.server.createUser(data).enqueue(new Callback<LoginData>() {
                                                @Override
                                                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                                    if (response.isSuccessful()) {
                                                        Log.d("Server.createUser", "onSuccess");
                                                        Intent intent = new Intent(mActivity, MainActivity.class);

                                                        mActivity.startActivity(intent);
                                                        mActivity.finish();
                                                    }
                                                    Log.d("Server.createUser", "fuck");
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