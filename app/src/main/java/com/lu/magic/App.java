package com.lu.magic;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.lu.magic.util.AppUtil;

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
        initSec();
        AppUtil.doInit(this);
    }

    private void initSec() {
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectNonSdkApiUsage()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects() //API等级11
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
    }
}