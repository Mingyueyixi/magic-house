package com.lu.code.foolish.egg.ui

import androidx.viewbinding.ViewBinding


class ViewBindingWrap<T : ViewBinding>(
    private var _binding: T? = null
) {
    val binding get() = _binding!!

    fun unbindRef() {
        _binding = null
    }
}