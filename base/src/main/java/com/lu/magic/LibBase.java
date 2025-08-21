package com.lu.magic;

import android.app.Application;
import android.content.Context;

import java.util.ResourceBundle;

/**
 * @author Lu
 * @date 2025/8/21
 * @description
 */
public class LibBase {
    private static Context sApplication;

    public static void init(Context application) {
        sApplication = application;
    }

    public static Context getApp() {
        return sApplication;
    }
}
