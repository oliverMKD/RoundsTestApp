package com.oliver.imagelibrary.cache

import android.graphics.Bitmap

internal interface ImageCache {
    fun put(url: String, bitmap: Bitmap)
    fun get(url: String): Bitmap?
    fun clear()
}