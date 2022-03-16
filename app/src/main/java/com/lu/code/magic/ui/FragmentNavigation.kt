package com.lu.code.magic.ui

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*

/**
 * @Author: Lu
 * Date: 2022/03/17
 * Description:
 */
class FragmentNavigation(
    private var activity: FragmentActivity,
    private var fragmentContainer: ViewGroup
) {

    private var fragmentStack: Stack<Fragment> = Stack()
    private val supportFragmentManager get() = activity.supportFragmentManager
    private var navigateIndex = -1

    fun navigate(fragment: Fragment) {
        var tag = fragment.javaClass.toString()

        var frag = supportFragmentManager.findFragmentByTag(tag)
        var transaction = supportFragmentManager.beginTransaction()

        //主要导航片段
        var primaryFragment = supportFragmentManager.primaryNavigationFragment
        if (primaryFragment != null) {
            transaction.hide(primaryFragment)
        }

        if (frag == null || fragment != frag) {
            frag = fragment
            transaction.add(fragmentContainer.id, frag, tag)
        } else {
            transaction.show(frag)
        }
        //设置主导航fragment为当前要显示的导航fragment
        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(true);
        transaction.commitNow();

        if (!fragmentStack.contains(frag) && navigateIndex < fragmentStack.size) {

            if (navigateIndex > -1 && navigateIndex != fragmentStack.size - 1) {
                //当前显示的fragment不是最后一个，清空当前fragment之后的fragment记录
                var i = navigateIndex + 1
                while (navigateIndex != fragmentStack.size - 1) {
                    fragmentStack.pop()
                }

            }
            fragmentStack.add(frag)

        }

        navigateIndex = fragmentStack.indexOf(frag)
    }

    fun findNavigateFragment(): Fragment? {
        return fragmentStack[navigateIndex]
    }

    fun navigateBack(): Boolean {
        var preIndex = navigateIndex - 1
        if (preIndex < 0 || preIndex >= fragmentStack.size) {
            return false
        }
        var frag = fragmentStack[preIndex]
        navigate(frag)
        return true
    }

    fun navigateFont(): Boolean {
        var preIndex = navigateIndex + 1
        if (preIndex < 0 || preIndex >= fragmentStack.size) {
            return false
        }
        var frag = fragmentStack[preIndex]
        navigate(frag)
        return true
    }

}
