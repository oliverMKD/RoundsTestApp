package com.oliver.imagelibrary.async

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

internal class Downloader(
    private val client: OkHttpClient
) {

    suspend fun download(url: String): Bitmap? = withContext(Dispatchers.IO)
    {
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.byteStream()?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } else {
                Log.e("Downloader", "Failed to fetch image: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e("Downloader", "Error downloading image: ${e.message}")
            null
        }
    }
}