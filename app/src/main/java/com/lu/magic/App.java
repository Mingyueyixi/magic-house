package com.lu.magic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: Application
 */
public class App extends Application {
    private static App instance = null;

    public static Context getInstance() {
        return instance;
    }

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ModuleRegistry.INSTANCE.apply();
        AppInitProxy.callInit(this);
    }

}