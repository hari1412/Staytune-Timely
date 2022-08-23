package com.buenatech.staytune.activities

import android.os.Bundle
import com.buenatech.staytune.R
import com.buenatech.staytune.utils.PrefHelper
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide


class MainIntroActivity : IntroActivity() {

    var prefHelper: PrefHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefHelper = PrefHelper(this)

        prefHelper!!.setFirstStart(this, "started")




        addIntroScreen(
            getString(R.string.intro_1_title), getString(R.string.intro_1_description),
            R.drawable.intro4
        )
        addIntroScreen(
            getString(R.string.intro_2_title), getString(R.string.intro_2_description),
            R.drawable.intro_1
        )
        addIntroScreen(
            getString(R.string.intro_3_title), getString(R.string.intro_3_description),
            R.drawable.reminder
        )
        addIntroScreen(
            getString(R.string.intro_4_title), getString(R.string.intro_4_description),
            R.drawable.disturb

        )



    }

    private fun addIntroScreen(title: String, description: String, drawable: Int) {
        addSlide(
            SimpleSlide.Builder()
                .title(title)
                .description(description)
                .image(drawable)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build()
        )
    }
}