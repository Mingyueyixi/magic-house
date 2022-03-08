package com.lu.code.magic.main;

import androidx.lifecycle.ViewModel;

/**
 * @Author: Lu
 * Date: 2022/03/08
 * Description:
 */
public class MagicConfigViewModel extends ViewModel {
    private AppListModel appListModel;

    public AppListModel getAppListModel() {
        return appListModel;
    }

    public void setAppListModel(AppListModel appListModel) {
        this.appListModel = appListModel;
    }
}
