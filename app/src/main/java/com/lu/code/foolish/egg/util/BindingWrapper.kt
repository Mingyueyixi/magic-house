package com.lu.code.foolish.egg.util

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.lu.code.foolish.egg.databinding.FragmentLogBinding
import com.lu.code.foolish.egg.main.LogFragment

class BindingWrapper :LifecycleObserver{

    fun inflate(fragment: Fragment, inflater: LayoutInflater) {
        FragmentLogBinding.inflate(inflater)
    }

}