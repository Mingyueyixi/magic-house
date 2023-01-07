package com.lu.magic

import androidx.fragment.app.Fragment
import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckScreenMagic
import com.lu.magic.screen.ScreenOrientationFragment

class ModuleScreen : IModuleFace {


    override fun loadMagic(): BaseMagic {
        return FuckScreenMagic()
    }

    override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
        return IModuleFace.IFragmentFactory {
            ScreenOrientationFragment()
        }
    }
}