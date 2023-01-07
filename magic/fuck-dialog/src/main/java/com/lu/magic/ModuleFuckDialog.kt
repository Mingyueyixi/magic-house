package com.lu.magic

import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckDialogMagic
import com.lu.magic.fuckdialog.FuckDialogFragment

class ModuleFuckDialog : IModuleFace {
    override fun loadMagic(): BaseMagic {
        return FuckDialogMagic()
    }

    override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
        return IModuleFace.IFragmentFactory { FuckDialogFragment() }
    }
}