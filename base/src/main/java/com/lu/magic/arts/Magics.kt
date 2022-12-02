package com.lu.magic.arts

import de.robv.android.xposed.callbacks.XC_LoadPackage

object Magics {

    val MAGIC_EMPTY = EmptyMagic()

    class EmptyMagic : BaseMagic() {
        override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {

        }

    }
}