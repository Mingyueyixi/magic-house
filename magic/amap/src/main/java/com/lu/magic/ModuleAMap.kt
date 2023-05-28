package com.lu.magic

import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckAMapLocationMagic

class ModuleAMap : IModuleFace {
    override fun loadMagic(): BaseMagic {
        return FuckAMapLocationMagic()
    }
}