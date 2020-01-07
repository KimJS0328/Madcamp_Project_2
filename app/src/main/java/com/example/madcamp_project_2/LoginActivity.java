package com.example.madcamp_project_2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private Context p2m = this;
    private Context mContext;
    private EditText id;
    private EditText pw;
    private EditText name;
    private Button login;
    private Button join;

    private LoginButton btn_facebook_login;
    private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;
    private Button btn_custom_login;

    private String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.SEND_SMS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int flag = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(permissions, 100);
                    flag = 1;
                    break;
                }
            }
            if (flag != 1) startAct();

        } else {
            startAct();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) finish();
            }
            startAct();
        }
    }

    private void startAct() {
        AccessToken token = AccessToken.getCurrentAccessToken();

        if (token != null && !token.isExpired()) {
            Log.d("login", token.getUserId());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        id = findViewById(R.id.ID);
        pw = findViewById(R.id.PW);
        name = findViewById(R.id.name);
        login = findViewById(R.id.login);
        join = findViewById(R.id.join);

        mContext = getApplicationContext();

        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new LoginCallback(this);

        btn_facebook_login = (LoginButton) findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_facebook_login.registerCallback(mCallbackManager, mLoginCallback);

        btn_custom_login = (Button) findViewById(R.id.btn_custom_login);
        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_facebook_login.performClick();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RetrofitConnection retrofitConnection = new RetrofitConnection();
                LoginData data = new LoginData();
                data.setName(name.getText().toString());
                data.setUserId(id.getText().toString());
                data.setPasswd(pw.getText().toString());
                retrofitConnection.server.getUser(data).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            SharedPreferences pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("id", response.body().getUserId());
                            editor.putString("name", response.body().getName());
                            editor.commit();
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {

                    }
                });
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitConnection retrofitConnection = new RetrofitConnection();
                LoginData data = new LoginData();
                data.setName(name.getText().toString());
                data.setUserId(id.getText().toString());
                data.setPasswd(pw.getText().toString());

                retrofitConnection.server.createUser(data).enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(p2m, "계정이 생성되었습니다", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


}