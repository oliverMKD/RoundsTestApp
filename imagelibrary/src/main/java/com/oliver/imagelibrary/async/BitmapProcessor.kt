package com.oliver.imagelibrary.async

import android.graphics.Bitmap

internal class BitmapProcessor {

    fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height
        val height = (targetHeight / aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, height, true)
    }
}
