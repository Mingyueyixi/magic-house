package com.lu.magic.ui

import androidx.appcompat.widget.Toolbar

abstract class BaseToolBarActivity : BaseActivity() {
    abstract fun getToolBar():Toolbar
}