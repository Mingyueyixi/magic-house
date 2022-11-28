package com.lu.magic.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    open fun getContext(): Context {
        return this
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //fix memory leak
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
                finishAfterTransition()
            }
        }

    }

}