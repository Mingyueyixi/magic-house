package com.lu.magic.ui;

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {


    private var mBackPressedCallBack: OnBackPressedCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            mBackPressedCallBack = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = onBackPressed()
                    if (!isEnabled) {
                        //不消费事件，默认调用Activity的回退事件
                        activity?.onBackPressed()
                    }
                }
            }
            it.onBackPressedDispatcher.addCallback(this, mBackPressedCallBack!!)
        }

    }

    /**
     * 与BaseActivity结合才有作用
     * return false 不消费事件，交由Activity处理
     * return true 消费事件，自行处理Activity不进行处理
     */
    open fun onBackPressed(): Boolean {
        return false
    }

}
