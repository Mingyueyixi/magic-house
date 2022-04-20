package com.lu.code.magic.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
//
//    /**
//     * 返回键监听
//     */
//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            super.onBackPressed()
//            return
//        }
//
//        val currentFragment = supportFragmentManager.fragments.last() as BaseFragment?
//        val backProcess = currentFragment?.onBackPressed()
//        if (backProcess == false) {
//            super.onBackPressed()
//        }
//    }

}