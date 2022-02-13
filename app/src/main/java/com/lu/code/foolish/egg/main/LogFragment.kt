package com.lu.code.foolish.egg.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lu.code.foolish.egg.databinding.FragmentLogBinding
import com.lu.code.foolish.egg.ui.BaseFragment

class LogFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingWrap = withBindingWrap {
            FragmentLogBinding.inflate(inflater, container, false)
        }
        return bindingWrap.binding.root
    }

}