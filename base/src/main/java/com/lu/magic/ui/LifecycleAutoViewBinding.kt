package com.lu.magic.ui;

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class LifecycleAutoViewBinding<F : Fragment, V : ViewBinding> : ReadWriteProperty<F, V>,
    LifecycleEventObserver {

    private var binding: V? = null

    override fun getValue(thisRef: F, property: KProperty<*>): V {
        binding?.let {
            return it
        }
        throw IllegalStateException("Can't access ViewBinding before onCreateView and after onDestroyView!")
    }

    override fun setValue(thisRef: F, property: KProperty<*>, value: V) {
        if (thisRef.viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            throw IllegalStateException("Can't set ViewBinding after onDestroyView!")
        }
        thisRef.viewLifecycleOwner.lifecycle.addObserver(this)
        binding = value
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            binding = null
            source.lifecycle.removeObserver(this)
        }
    }
}