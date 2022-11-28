package com.lu.magic

import com.lu.magic.IModuleFace.ModuleEmpty
import com.lu.magic.amap.FuckAMapFragment
import com.lu.magic.arts.BaseMagic
import com.lu.magic.arts.FuckAMapLocationMagic
import com.lu.magic.arts.FuckDialogMagic
import com.lu.magic.arts.FuckScreenMagic
import com.lu.magic.arts.FuckVibratorMagic
import com.lu.magic.fuckdialog.FuckDialogFragment
import com.lu.magic.screen.ScreenOrientationFragment
import com.lu.magic.util.config.SheetName

object ModuleRegistry {

    fun apply() {
        ModuleProviders.put(SheetName.EMPTY, ModuleEmpty())
        ModuleProviders.put(SheetName.FUCK_VIBRATOR) { FuckVibratorMagic() }
        ModuleProviders.put(SheetName.FUCK_DIALOG, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckDialogMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory { FuckDialogFragment() }
            }
        })
        ModuleProviders.put(SheetName.FUCK_SCREEN_ORIENTATION, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckScreenMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory {
                    ScreenOrientationFragment()
                }
            }
        })
        ModuleProviders.put(SheetName.AMAP_LOCATION, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckAMapLocationMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory {
                    FuckAMapFragment()
                }
            }
        })
    }

}