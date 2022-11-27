package com.lu.magic

import com.lu.magic.IModuleFace.ModuleEmpty

object ModuleRegistry {

    fun register() {
        ModuleProviders.put(
            ModuleEmpty(),
            ModuleFuckVibrator(),
            ModuleAMap(),
            ModuleScreen()
        )

    }
}