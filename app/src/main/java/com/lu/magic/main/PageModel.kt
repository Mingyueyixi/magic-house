package com.lu.magic.main

import androidx.core.util.Supplier
import com.lu.magic.ui.BaseFragment


class PageModel(
    var iconId: Int,
    var textId: Int,
    var fragmentProvider: Supplier<out BaseFragment>
) {

    fun createFragment(): BaseFragment {
        return fragmentProvider.get()
    }

}