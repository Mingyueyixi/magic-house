package com.lu.magic.arts;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestMagic extends BaseMagic {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//
//        XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.app.Activity"),
//                "onResume",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        ConfigUtil.init((Context) param.thisObject);
//                        handlePanQie();
//                    }
//                }
//        );
    }

    private void handlePanQie() {
//        Map<String, Object> data;
//        try {
//            data = ConfigUtil.getAll();
//            Log.e("panqie", GsonUtil.toJson(data));
//            ToastUtil.show(GsonUtil.toJson(data));
//        } catch (Exception e) {
//            ToastUtil.show(Log.getStackTraceString(e));
//            Log.e("panqie", "", e);
//        }

    }


}
