package com.lu.magic.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object MediaStoreUtil {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun queryDownload(context: Context, relativeDir: String, fileName: String): Cursor? {
        return query(context, MediaStore.Downloads.EXTERNAL_CONTENT_URI, relativeDir, fileName)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun query(context: Context, uri: Uri, relativeDir: String, fileName: String): Cursor? {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.RELATIVE_PATH
        )
        //条件：显示名称和相对路径
        val selection =
            "${MediaStore.Images.Media.DISPLAY_NAME} =? AND (${MediaStore.Images.Media.RELATIVE_PATH} =? OR ${MediaStore.Images.Media.RELATIVE_PATH} =?)"
        val selectionArgs = arrayOf(fileName, relativeDir, "$relativeDir/")
//        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        return resolver.query(uri, projection, selection, selectionArgs, null)
    }

}