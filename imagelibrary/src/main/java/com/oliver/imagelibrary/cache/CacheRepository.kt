package com.oliver.imagelibrary.cache

import android.content.Context
import android.graphics.Bitmap

internal class CacheRepository(
    context: Context,
    memoryCacheSize: Int = 1024 * 1024 * 4
) : ImageCache {

    private val memoryCache = MemoryCache(memoryCacheSize)
    private val diskCache = DiskCache(context)

    override suspend fun put(url: String, bitmap: Bitmap) {
        memoryCache.put(url, bitmap)
        diskCache.put(url, bitmap)
    }

    override suspend fun get(url: String): Bitmap? {
        memoryCache.get(url)?.let {
            return it
        }
        diskCache.get(url)?.let {
            memoryCache.put(url, it)
            return it
        }
        return null
    }

    override suspend fun clear() {
        memoryCache.clear()
        diskCache.clear()
    }
}
