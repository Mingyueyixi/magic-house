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

object ModuleRegistry {
    /** 空实现 */
    const val KEY_EMPTY = ModuleEmpty.MODULE_KEY

    /** 震动模块 */
    const val KEY_FUCK_VIBRATOR = "KEY_FUCK_VIBRATOR"

    /** 对话框模块 */
    const val KEY_FUCK_DIALOG = "KEY_FUCK_DIALOG"

    /** 高德地图模块 */
    const val KEY_FUCK_AMAP = "KEY_FUCK_AMAP"

    /** 屏幕旋转模块 */
    const val KEY_FUCK_SCREEN_ROTATE = "KEY_FUCK_SCREEN_ROTATE"


    fun apply() {
        ModuleProviders.put(KEY_EMPTY, ModuleEmpty())
        ModuleProviders.put(KEY_FUCK_VIBRATOR) { FuckVibratorMagic() }
        ModuleProviders.put(KEY_FUCK_DIALOG, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckDialogMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory { FuckDialogFragment() }
            }
        })
        ModuleProviders.put(KEY_FUCK_SCREEN_ROTATE, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckScreenMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory {
                    ScreenOrientationFragment()
                }
            }
        })
        ModuleProviders.put(KEY_FUCK_AMAP, object : IModuleFace {
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