package com.buenatech.staytune.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.buenatech.staytune.R
import com.buenatech.staytune.signinproviders.FirstActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        dataholder = application as DataHolder
        setContentView(R.layout.activity_splash)
        goToMain()

    }

    private fun goToMain() {
        Handler().postDelayed({
            val mIntent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(
                mIntent
            )
            finish()
        }, 1000)
    }
}