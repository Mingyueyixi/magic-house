package com.lu.magic

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lu.magic.ui.BaseActivity


open class BaseUIActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setFitContent()
    }

    open fun isContentFitSystemWindows() = true

    override fun setContentView(layoutResID: Int) {
        val view = layoutInflater.inflate(layoutResID, null)
        this.setContentView(view)

    }
    override fun setContentView(view: View?) {
        super.setContentView(view)
        setFitContent(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setFitContent(view)
    }

    private fun setFitContent(root: View?) {
        if (root == null) {
            return
        }
        // 设置透明导航栏
        val window: Window = getWindow()
        window.getDecorView().setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(android.R.color.transparent)
        //window.setNavigationBarColor(getColor(R.color.transparent))
//        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (isContentFitSystemWindows()) {
                root.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            }
            onApplyWindowInsets(view, insets, systemBars)
            return@setOnApplyWindowInsetsListener insets
        }

    }

    open protected fun onApplyWindowInsets(content: View, insets: WindowInsetsCompat, systemBars: Insets) {

    }

}