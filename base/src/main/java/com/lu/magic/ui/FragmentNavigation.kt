package com.lu.magic.ui

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*


/**
 * @Author: Lu
 * Date: 2022/03/17
 * Description: 简易的fragment导航工具类
 */
class FragmentNavigation : DefaultLifecycleObserver {
    private lateinit var fragmentContainer: ViewGroup
    private lateinit var supportFragmentManager: FragmentManager
    private var fragmentStack: Stack<Fragment> = Stack()
    private var navigateIndex = -1

    constructor(activity: FragmentActivity, fragmentContainer: ViewGroup) {
        doInit(activity, fragmentContainer)
    }

    private fun doInit(activity: FragmentActivity, fragmentContainer: ViewGroup) {
        this.supportFragmentManager = activity.supportFragmentManager
        this.fragmentContainer = fragmentContainer
        activity.lifecycle.addObserver(this)
    }

    /**
     * 导航到指定cls
     */
    fun navigate(fragClass: Class<out Fragment>) {
        var tag = fragClass.toString()
        var frag: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (frag == null) {
            frag = fragClass.newInstance()
        }
        navigate(frag!!)
    }

    /**
     * 导航到指定fragment
     */
    fun navigate(fragment: Fragment) {
        var tag = fragment.javaClass.toString()
        navigateInterval(fragment, tag)
    }


    private fun navigateInterval(fragment: Fragment, tag: String) {
        var transaction = supportFragmentManager.beginTransaction()
        //主导航Fragment
        var primaryFragment = supportFragmentManager.primaryNavigationFragment
        if (primaryFragment != null) {
            transaction.hide(primaryFragment)
        }
        if (!fragment.isAdded) {
            transaction.add(fragmentContainer.id, fragment, tag)
        }
        //设置主导航fragment为当前要显示的导航fragment
        transaction.setPrimaryNavigationFragment(fragment);
        transaction.show(fragment).commitNow()

        //处理自己维护的fragment
        if (!fragmentStack.contains(fragment) && navigateIndex < fragmentStack.size) {

            if (navigateIndex > -1 && navigateIndex != fragmentStack.size - 1) {
                //当前显示的fragment不是最后一个，清空当前fragment之后的fragment记录
                var i = navigateIndex + 1
                while (navigateIndex != fragmentStack.size - 1) {
                    fragmentStack.pop()
                }

            }
            fragmentStack.add(fragment)
        }

        navigateIndex = fragmentStack.indexOf(fragment)
    }

    fun findNavigateFragment(): Fragment? {
        return fragmentStack[navigateIndex]
    }

    /**
     * 退后
     */
    fun navigateBack(): Boolean {
        var preIndex = navigateIndex - 1
        if (preIndex < 0 || preIndex >= fragmentStack.size) {
            return false
        }
        var frag = fragmentStack[preIndex]
        navigate(frag)
        return true
    }

    /**
     * 前进
     */
    fun navigateFront(): Boolean {
        var preIndex = navigateIndex + 1
        if (preIndex < 0 || preIndex >= fragmentStack.size) {
            return false
        }
        var frag = fragmentStack[preIndex]
        navigate(frag)
        return true
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        fragmentStack.clear()
    }
}
