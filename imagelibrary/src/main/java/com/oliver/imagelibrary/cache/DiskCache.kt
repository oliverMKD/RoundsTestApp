package com.oliver.imagelibrary.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal class DiskCache(
    context: Context,
) : ImageCache {

    private val cacheDir: File = File(context.cacheDir, "image_cache").apply {
        if (!exists()) mkdirs()
    }

    private val expirationTime = 14400000

    override suspend fun get(url: String): Bitmap? {
        val key = md5(url)
        val imageFile = File(cacheDir, key)
        val metadataFile = File(cacheDir, "$key.meta")

        if (imageFile.exists() && metadataFile.exists()) {
            if (isExpired(metadataFile)) {
                imageFile.delete()
                metadataFile.delete()
                return null
            }

            return BitmapFactory.decodeFile(imageFile.absolutePath)
        }

        return null
    }

    override suspend fun put(url: String, bitmap: Bitmap) {
        val key = md5(url)
        val imageFile = File(cacheDir, key)
        val metadataFile = File(cacheDir, "$key.meta")

        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(imageFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
            }
            metadataFile.writeText(System.currentTimeMillis().toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override suspend fun clear() {
        cacheDir.listFiles()?.forEach { file ->
            deleteRecursively(file)
        }
    }

    private fun md5(url: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(url.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    private fun isExpired(metadataFile: File): Boolean {
        return try {
            val lastModifiedTime = metadataFile.readText().toLong()
            val currentTime = System.currentTimeMillis()
            (currentTime - lastModifiedTime) > expirationTime
        } catch (e: Exception) {
            true
        }
    }

    private fun deleteRecursively(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursively(child)
            }
        }
        file.delete()
    }
}