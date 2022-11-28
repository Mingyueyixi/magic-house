package com.lu.magic.ui;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

open abstract class BindingFragment<T : ViewBinding> : BaseFragment() {

    private var _binding: T? = null
    protected var binding: T
        get() = _binding!!
        set(value) {
            _binding = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = onViewBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    abstract fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T

    override fun onDestroyView() {
        super.onDestroyView()
        //需要防止内存泄露，binding生命周期比Fragment长：https://developer.android.google.cn/topic/libraries/view-binding
        //get时强制断言不为空，只是方便调用。实际存在空指针风险，因为onDestroyView时需要置空
        _binding = null
    }

}
