package com.example.madcamp_project_2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.security.MessageDigest


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

        callPermission();
        userId = intent.getStringExtra("USER_ID")

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
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

    private fun callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_READ_CONTACTS
            )

        } else {
            isPermission = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_READ_EXTERNAL_STORAGE
            )
        } else {
            isPermission = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION),
                PERMISSIONS_ACCESS_MEDIA_LOCATION
            )
        } else {
            isPermission = true
        }
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
