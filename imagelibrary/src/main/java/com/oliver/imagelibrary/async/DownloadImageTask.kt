package com.oliver.imagelibrary.async

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.oliver.imagelibrary.cache.CacheRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal class DownloadImageTask(
    private val url: String,
    private val imageView: ImageView,
    private val cacheRepository: CacheRepository
) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val downloader = Downloader(okHttpClient)
    private val bitmapProcessor = BitmapProcessor()

    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cachedBitmap = cacheRepository.get(url)
                if (cachedBitmap != null) {
                    updateImageView(cachedBitmap)
                    return@launch
                }

                val bitmap = downloader.download(url)
                bitmap?.let {
                    val resizedBitmap =
                        bitmapProcessor.resizeBitmap(
                            it,
                            imageView.width,
                            imageView.height
                        )
                    cacheRepository.put(url, resizedBitmap)
                    updateImageView(resizedBitmap)
                } ?: run {
                    Log.e("DownloadImageTask", "Failed to download image: $url")
                }
            } catch (e: Exception) {
                Log.e("DownloadImageTask", "Error: ${e.message}")
            }
        }
    }

    private suspend fun updateImageView(bitmap: Bitmap) {
        withContext(Dispatchers.Main) {
            imageView.setImageBitmap(bitmap)
        }
    }
}
