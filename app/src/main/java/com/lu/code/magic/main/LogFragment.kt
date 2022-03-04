package com.lu.code.magic.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lu.code.magic.magic.databinding.FragmentLogBinding
import com.lu.code.magic.ui.BindingFragment

class LogFragment : BindingFragment<FragmentLogBinding>() {
    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLogBinding {
        return FragmentLogBinding.inflate(inflater, container, false)
    }

}