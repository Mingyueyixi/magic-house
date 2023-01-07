package com.lu.magic.util.dialog;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {

    /**
     * 显示编辑的Dialog提示框
     *
     * @return
     */
    public static EditDialog.Builder buildEditDialog(Context context) {
        return new EditDialog.Builder(context);
    }

    public static AlertDialog.Builder buildAlertDialog(Context context) {
        return new AlertDialog.Builder(context);
    }
}
