package com.lu.magic.main.fuckdialog;

/**
 * @Author: Lu
 * Date: 2022/03/01
 * Description:
 */
public class FuckDialogModel {
    private Item fuckCanCancel;
    private Item disableShow;

    public static class Item {
        private String name;
        private String description;
        private boolean enable;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public Item getFuckCanCancel() {
        return fuckCanCancel;
    }

    public void setFuckCanCancel(Item fuckCanCancel) {
        this.fuckCanCancel = fuckCanCancel;
    }

    public Item getDisableShow() {
        return disableShow;
    }

    public void setDisableShow(Item disableShow) {
        this.disableShow = disableShow;
    }
}
