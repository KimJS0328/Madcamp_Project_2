package com.example.madcamp_project_2

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.security.MessageDigest
import android.content.Intent as Intent1


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_READ_CONTACTS = 1000
    private val PERMISSIONS_READ_EXTERNAL_STORAGE = 1001
    private val PERMISSIONS_ACCESS_MEDIA_LOCATION = 1002
    private var isPermission = false
    public lateinit var userId: String

    private var backPressedTime: Long = 0
    private var mOnBackPressedListener: onBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        userId = pref.getString("id", "").toString()

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter
        viewpager_main.offscreenPageLimit = 2

        tabs_main.setupWithViewPager(viewpager_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> { return onLogout() }
            else -> { return super.onOptionsItemSelected(item) }
        }
    }

    private fun onLogout() : Boolean {
        LoginManager.getInstance().logOut()
        val intent = Intent1(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

    interface onBackPressedListener {
        fun onBack()
    }

    fun setOnBackPressedListener(mListener: onBackPressedListener?) {
        mOnBackPressedListener = mListener
    }

    override fun onBackPressed() {
        if (mOnBackPressedListener == null) {
            if (backPressedTime == 0.toLong()) {
                Toast.makeText(this, "Exit when back pressed once more", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
            else {
                var seconds = System.currentTimeMillis() - backPressedTime

                if (seconds > 2000.toLong()) {
                    Toast.makeText(this, "Exit when back pressed once more", Toast.LENGTH_SHORT).show()
                    backPressedTime = System.currentTimeMillis()
                }
                else {
                    super.onBackPressed()
                }
            }

        }
        else {
            mOnBackPressedListener!!.onBack();
        }
    }

}
