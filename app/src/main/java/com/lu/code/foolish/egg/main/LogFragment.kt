package com.lu.code.foolish.egg.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lu.code.foolish.egg.databinding.FragmentLogBinding
import com.lu.code.foolish.egg.ui.BindingFragment

class LogFragment : BindingFragment<FragmentLogBinding>() {
    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLogBinding {
        return FragmentLogBinding.inflate(inflater, container, false)
    }

}