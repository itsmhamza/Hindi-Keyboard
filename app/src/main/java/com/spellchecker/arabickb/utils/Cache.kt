package com.spellchecker.arabickb.utils

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Cache {

    private const val CHILD_DIR = "images"
    private const val TEMP_FILE_NAME = "img"
    private const val FILE_EXTENSION = ".png"
    private const val COMPRESS_QUALITY = 100


    fun Activity.saveImgToCache(bitmap: Bitmap, name: String?): File? {
        var cachePath: File? = null
        var fileName: String? = TEMP_FILE_NAME

        if (!TextUtils.isEmpty(name)) {
            fileName = name
        }

        try {
            cachePath = File(getCacheDir(), CHILD_DIR)
            cachePath.mkdirs()
            val stream = FileOutputStream(cachePath.toString() + "/" + fileName + FILE_EXTENSION)
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, stream)
            stream.close()
        } catch (e: IOException) {

        }
        return cachePath
    }
}