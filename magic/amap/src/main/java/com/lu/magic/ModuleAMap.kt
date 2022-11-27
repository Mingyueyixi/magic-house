package com.lu.magic

import com.lu.magic.IModuleFace.IFragmentFactory
import com.lu.magic.amap.FuckAMapFragment
import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckAMapLocationMagic

class ModuleAMap : IModuleFace {
    override fun loadMagic(): BaseMagic {
        return FuckAMapLocationMagic()
    }

    override fun getDetailFragmentFactory(): IFragmentFactory {
        return IFragmentFactory {
            FuckAMapFragment()
        }
    }
}