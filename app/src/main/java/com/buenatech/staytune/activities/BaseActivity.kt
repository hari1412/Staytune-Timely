package com.buenatech.staytune.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.buenatech.staytune.R
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {


        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)


    }


    private var toolbar: MaterialToolbar? = null


    protected fun setPersistentActionBar(toolbar: MaterialToolbar?) {
        this.toolbar = toolbar
        toolbar!!.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        setSupportActionBar(toolbar)
        this.toolbar?.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected fun setToolbarTitle(@StringRes id: Int) {
        this.toolbar?.title = getString(id)
    }

}