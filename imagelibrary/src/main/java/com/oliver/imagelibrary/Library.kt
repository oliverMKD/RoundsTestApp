package com.oliver.imagelibrary

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.oliver.imagelibrary.async.DownloadImageTask
import com.oliver.imagelibrary.cache.CacheRepository


class Library internal constructor(context: Context) {

    private val cacheRepository = CacheRepository(context)

    fun loadImage(
        url: String,
        imageView: ImageView,
        placeholder: Int? = null
    ) {
        placeholder?.let { imageView.setImageResource(it) }
        DownloadImageTask(url, imageView, cacheRepository).execute()
    }

    fun clearCache() {
        cacheRepository.clear()
    }
}