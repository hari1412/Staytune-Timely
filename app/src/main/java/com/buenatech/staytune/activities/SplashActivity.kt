package com.buenatech.staytune.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.buenatech.staytune.R
import com.buenatech.staytune.signinproviders.FirstActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        dataholder = application as DataHolder
        goToMain()

    }

    private fun goToMain() {
        Handler().postDelayed({
            val mIntent = Intent(this@SplashActivity, FirstActivity::class.java)
            startActivity(
                mIntent
            )
            finish()
        }, 1000)
    }
}