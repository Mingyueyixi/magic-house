package com.lu.magic.util

import android.os.Bundle
import androidx.fragment.app.Fragment

class FragmentUtil {
    companion object {

        inline fun <reified T : Fragment> newInstance(): T {
            return T::class.java.newInstance()
        }

        inline fun <reified T : Fragment> newInstance(args: Bundle?): T {
            val instance = T::class.java.newInstance()
            if (args != null) {
                instance.arguments = args
            }
            return instance as T
        }

    }
}
