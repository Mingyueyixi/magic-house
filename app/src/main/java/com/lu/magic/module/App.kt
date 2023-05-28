package com.lu.magic.module

import android.app.Application
import android.content.Context
import com.lu.magic.config.ConfigUtil
import com.lu.magic.frame.xp.SPreference

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: Application
 */
class App : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        ModuleRegistry.apply()

        SPreference.initServer(this, 10087)
        ConfigUtil.init(this)
        AppInitProxy.callInit(this)
    }

    companion object {
        private var instance: App? = null
        fun getInstance(): Context? {
            return instance
        }
    }
}