package com.oliver.imagelibrary.cache

import android.graphics.Bitmap

internal interface ImageCache {
    suspend fun put(url: String, bitmap: Bitmap)
    suspend fun get(url: String): Bitmap?
    suspend fun clear()
}