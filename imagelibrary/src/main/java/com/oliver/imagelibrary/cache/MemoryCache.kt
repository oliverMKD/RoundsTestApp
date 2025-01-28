package com.oliver.imagelibrary.cache

import android.graphics.Bitmap
import android.util.LruCache
import com.oliver.imagelibrary.cache.model.CacheEntry

internal class MemoryCache(
    newMaxSize: Int
) : ImageCache {

    private val cache: LruCache<String, CacheEntry>
    private val cacheDurationMillis = 14400000

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val defaultCacheSize = maxMemory / 2
        val cacheSize = when {
            newMaxSize <= 0 -> defaultCacheSize
            newMaxSize > maxMemory -> defaultCacheSize
            else -> newMaxSize
        }

        cache = object : LruCache<String, CacheEntry>(cacheSize) {
            override fun sizeOf(key: String?, value: CacheEntry): Int {
                return (value.bitmap.byteCount / 1024).coerceAtLeast(1)
            }
        }
    }

    override fun put(url: String, bitmap: Bitmap) {
        val entry = CacheEntry(bitmap, System.currentTimeMillis())
        cache.put(url, entry)
    }

    override fun get(url: String): Bitmap? {
        val entry = cache.get(url)
        if (entry != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - entry.timestamp > cacheDurationMillis) {
                cache.remove(url)
                return null
            }
            return entry.bitmap
        }
        return null
    }

    override fun clear() {
        cache.evictAll()
    }
}
