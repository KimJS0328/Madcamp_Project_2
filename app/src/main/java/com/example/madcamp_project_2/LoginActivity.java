package com.example.madcamp_project_2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;



public class LoginActivity extends AppCompatActivity {

    private Context mContext;
    private LoginButton btn_facebook_login;
    private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;
    private String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.SEND_SMS};

    private Button btn_custom_login;

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
            intent.putExtra("USER_ID", token.getUserId());
            startActivity(intent);
            finish();
        }


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
    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}