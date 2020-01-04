package com.example.madcamp_project_2

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter
        viewpager_main.offscreenPageLimit = 2

        tabs_main.setupWithViewPager(viewpager_main)

        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)

            for (signature in info.signatures ) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val str = Base64.encode(md.digest(), 0)
                Log.e("Hash key", String(str))
            }
        }
        catch (e: Exception) {}

    }

}
