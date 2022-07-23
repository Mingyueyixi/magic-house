package com.lu.magic.util;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description:
 */
public class ClassUtil {

    public static List<String> getClassList(Context context, String packageName) {
        ArrayList<String> classes = new ArrayList<String>();
        try {
            //PathClassLoader 暂时找不到替代方法，查找包下类名。考虑注解
            //https://stackoverflow.com/questions/50120303/how-can-i-use-pathclassloader-to-replace-the-deprecated-dexfile-apis
            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> entries = df.entries();
            while (entries.hasMoreElements()) {
                String className = (String) entries.nextElement();
                if (className.contains(packageName)) {
                    classes.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }


}
