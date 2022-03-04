package com.lu.code.magic.util

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.lu.code.magic.magic.databinding.FragmentLogBinding

class BindingWrapper :LifecycleObserver{

    fun inflate(fragment: Fragment, inflater: LayoutInflater) {
        FragmentLogBinding.inflate(inflater)
    }

}