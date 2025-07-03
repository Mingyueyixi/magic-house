package com.lu.magic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lu.magic.ui.BaseActivity

open class BaseUIActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFitContent()
    }

    open fun getFitTitleBar(): View? {
        return null
    }

    open fun isContentFitSystemWindows() = true


    private fun setFitContent() {
        val root = findViewById< View>(android.R.id.content)
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