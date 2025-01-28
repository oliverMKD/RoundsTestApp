package com.oliver.imagelibrary.async

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.oliver.imagelibrary.cache.CacheRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class DownloadImageTask(
    private val url: String,
    private val imageView: ImageView,
    private val cache: CacheRepository
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check cache first
            val cachedBitmap = cache.get(url)
            if (cachedBitmap != null) {
                updateImageView(imageView, cachedBitmap)
                return@launch
            } else {
                val bitmap = downloadImage(imageView, url)
                bitmap?.let {
                    cache.put(url, it)
                    updateImageView(imageView, it)
                }
            }
        }
    }

    private fun downloadImage(imageView: ImageView, url: String): Bitmap? {
        return try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                inputStream?.use { stream ->
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeStream(stream, null, options)

                    val targetWidth = imageView.width.takeIf { it > 0 } ?: 500
                    val targetHeight = imageView.height.takeIf { it > 0 } ?: 500
                    options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)

                    val imageRequest = client.newCall(request).execute()
                    val newStream = imageRequest.body?.byteStream()

                    options.inJustDecodeBounds = false
                    newStream?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    }
                }
            } else {
                Log.e("DownloadImage", "Failed to fetch image: ${response.code}")
                null
            }
        } catch (e: SocketTimeoutException) {
            Log.e("DownloadImage", "Connection timed out: ${e.message}")
            null
        } catch (e: IOException) {
            Log.e("DownloadImage", "Error downloading image: ${e.message}")
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private suspend fun updateImageView(imageView: ImageView, bitmap: Bitmap) {
        withContext(Dispatchers.Main) {
            imageView.setImageBitmap(bitmap)
        }
    }
}
