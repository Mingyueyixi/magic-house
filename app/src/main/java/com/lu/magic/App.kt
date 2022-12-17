package com.lu.magic

import android.app.Application
import android.content.Context
import com.lu.magic.ModuleRegistry.apply
import com.lu.magic.frame.xp.annotation.PreferenceIdValue
import com.lu.magic.frame.xp.BRPreference
import com.lu.magic.frame.xp.broadcast.BRConfig
import com.lu.magic.util.AppUtil

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
        apply()
        AppInitProxy.callInit(this)

        val sp = BRPreference(
            this, "config", PreferenceIdValue.SP, BRConfig(
                "com.lu.magic.server", "com.lu.magic.client"
            )
        )
        BRPreference.registerAsServer(AppUtil.getContext(), sp.config)

    }

    companion object {
        private var instance: App? = null
        fun getInstance(): Context? {
            return instance
        }
    }
}