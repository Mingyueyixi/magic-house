package com.lu.magic

import androidx.fragment.app.Fragment
import com.lu.magic.amap.FuckAMapFragment
import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckAMapLocationMagic

class ModuleAMap : IModuleFace {
    override fun loadMagic(): BaseMagic {
        return FuckAMapLocationMagic()
    }

    override fun getDetailFragment(): Fragment {
        return FuckAMapFragment()
    }
}