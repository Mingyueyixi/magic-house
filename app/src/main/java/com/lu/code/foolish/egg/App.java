package com.lu.code.foolish.egg;

import android.app.Application;
import android.content.Context;

import com.lu.code.foolish.egg.util.AppUtil;

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
        AppUtil.init(this);
    }

}