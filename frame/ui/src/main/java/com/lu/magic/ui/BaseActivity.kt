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
        //fix memory leak，似乎没修复成功
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
                finishAfterTransition()
            }
        }

    }

}