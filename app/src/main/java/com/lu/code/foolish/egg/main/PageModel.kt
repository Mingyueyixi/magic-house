package com.lu.code.foolish.egg.main

import androidx.core.util.Supplier
import com.lu.code.foolish.egg.ui.BaseFragment


class PageModel(
    var iconId: Int,
    var textId: Int,
    var fragmentProvider: Supplier<out BaseFragment>
) {

    fun createFragment(): BaseFragment {
        return fragmentProvider.get()
    }

}