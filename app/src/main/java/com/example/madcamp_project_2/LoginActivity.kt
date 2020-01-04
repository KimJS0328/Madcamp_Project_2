package com.example.madcamp_project_2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val mCallbackManager = CallbackManager.Factory.create()
    private val mLoginCallback = LoginCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        Log.e("success", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        btn_facebook_login.setReadPermissions(listOf("public_profile", "email"))
        btn_facebook_login.registerCallback(mCallbackManager, mLoginCallback)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}