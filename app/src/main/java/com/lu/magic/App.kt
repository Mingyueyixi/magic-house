package com.lu.magic

import com.lu.magic.config.ConfigUtil
import com.lu.magic.feat.AdbConnectionReceiver
import com.lu.magic.frame.xp.SPreference

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: Application
 */
class App : android.app.Application() {
    init {
        instance = this
    }

    override fun getSystemServiceName(serviceClass: Class<*>): String? {
        return super.getSystemServiceName(serviceClass)
    }
    override fun onCreate() {
        super.onCreate()
        ModuleRegistry.apply()

        SPreference.initServer(this, 10087)
        ConfigUtil.init(this)
        AdbConnectionReceiver.getInstance().register(this)
        AppInitProxy.callInit(this)
    }

    companion object {
        private var instance: App? = null

        @JvmStatic
        fun instance(): App {
            return instance!!
        }

    }
}