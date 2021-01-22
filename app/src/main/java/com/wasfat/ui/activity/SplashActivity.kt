package com.wasfat.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wasfat.R
import com.wasfat.utils.SessionManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 1500
    private val REQUEST_PERMISSION_SETTING = 101
    private val EXTERNAL_NETWORK_PERMISSION_CONSTANT = 100
    private var permissionStatus: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        permissionStatus = getSharedPreferences("permission", 0)
        startSplashTimer()
    }


    private fun startSplashTimer() {

        Handler().postDelayed({
            if (SessionManager.getInstance(this@SplashActivity).isLogin) {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                SelectLanguageActivity.startActivity(this@SplashActivity, true)
            /*    var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)*/
            }
            finish()

        }, SPLASH_TIME_OUT)
    }


    private fun getHashKey(): String? {
        Log.d("1234:", "hashkey2")
        var str_HashKey: String? = null
        try {
            Log.d("1234:", "hashkey3")
            val info = packageManager.getPackageInfo(
                "com.os.urservice",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("1234:", "hashkey4: " + Base64.encodeToString(md.digest(), Base64.DEFAULT))
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("1234:", "hashkey5: " + e.message)
        } catch (e: NoSuchAlgorithmException) {
            Log.d("1234:", "hashkey6: " + e.message)
        }


        return str_HashKey
    }


}