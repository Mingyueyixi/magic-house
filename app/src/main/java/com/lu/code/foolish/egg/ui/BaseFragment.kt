package com.lu.code.foolish.egg.ui;

import androidx.core.util.Supplier
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

open class BaseFragment : Fragment() {

    lateinit var bindingWrap: ViewBindingWrap<out ViewBinding>

     fun <T : ViewBinding> withBindingWrap(func: Supplier<T>): ViewBindingWrap<T> {
        var viewBinding = func.get()
        bindingWrap = ViewBindingWrap(viewBinding)
        return bindingWrap as ViewBindingWrap<T>
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //需要防止内存泄露，binding生命周期比Fragment长：https://developer.android.google.cn/topic/libraries/view-binding
        //get时强制断言不为空，只是方便调用。实际存在空指针风险，因为onDestroyView时需要置空
        bindingWrap.unbindRef()
    }

}
