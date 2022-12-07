package com.lu.magic

import android.content.Context
import com.lu.magic.IModuleFace.ModuleEmpty
import com.lu.magic.amap.FuckAMapFragment
import com.lu.magic.arts.*
import com.lu.magic.catchlog.LogViewFragment
import com.lu.magic.config.ModuleId
import com.lu.magic.fuckdialog.FuckDialogFragment
import com.lu.magic.main.AppRouter
import com.lu.magic.screen.ScreenOrientationFragment
import com.lu.magic.store.ItemModel

object ModuleRegistry {

    fun apply() {
        ModuleProviders.put(ModuleId.EMPTY, ModuleEmpty())
        ModuleProviders.put(ModuleId.FUCK_VIBRATOR) { FuckVibratorMagic() }
        ModuleProviders.put(ModuleId.FUCK_DIALOG, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckDialogMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory { FuckDialogFragment() }
            }
        })
        ModuleProviders.put(ModuleId.FUCK_SCREEN_ORIENTATION, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckScreenMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory {
                    ScreenOrientationFragment()
                }
            }
        })
        ModuleProviders.put(ModuleId.AMAP_LOCATION, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                return FuckAMapLocationMagic()
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                return IModuleFace.IFragmentFactory {
                    FuckAMapFragment()
                }
            }
        })
        ModuleProviders.put(ModuleId.DEVELOP_CATCH_LOG, object : IModuleFace {
            override fun loadMagic(): BaseMagic {
                //这个模块暂不需要magic hook
                return Magics.MAGIC_EMPTY
            }

            override fun onEntry(context: Context?, itemModel: ItemModel?) {
                if (context != null && itemModel != null) {
                    //直接进入DetailConfigActivity
                    AppRouter.routeDetailConfigPage(context, itemModel, null)
                }
            }

            override fun getDetailFragmentFactory(): IModuleFace.IFragmentFactory {
                //为DetailConfigActivity配置fragment
                return IModuleFace.IFragmentFactory { LogViewFragment() }
            }
        })
    }

}