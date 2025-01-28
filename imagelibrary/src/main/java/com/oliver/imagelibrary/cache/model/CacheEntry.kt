package com.oliver.imagelibrary.cache.model

import android.graphics.Bitmap

data class CacheEntry(
    val bitmap: Bitmap,
    val timestamp: Long
)
