package com.oliver.imagelibrary

import android.content.Context
import android.widget.ImageView
import com.oliver.imagelibrary.async.DownloadImageTask
import com.oliver.imagelibrary.cache.CacheRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class Library internal constructor(context: Context) {

    private val cacheRepository = CacheRepository(context)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun loadImage(
        url: String,
        imageView: ImageView,
        placeholder: Int? = null
    ) {
        placeholder?.let { imageView.setImageResource(it) }
        DownloadImageTask(url, imageView, cacheRepository).execute()
    }

    fun clearCache() {
        scope.launch {
            cacheRepository.clear()
        }
    }
}