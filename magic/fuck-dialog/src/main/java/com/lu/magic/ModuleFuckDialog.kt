package com.lu.magic

import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckDialogMagic

class ModuleFuckDialog : IModuleFace {
    override fun loadMagic(): BaseMagic {
        return FuckDialogMagic()
    }

}