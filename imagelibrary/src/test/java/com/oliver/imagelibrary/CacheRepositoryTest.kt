package com.oliver.imagelibrary

import android.content.Context
import android.graphics.Bitmap
import com.oliver.imagelibrary.cache.CacheRepository
import com.oliver.imagelibrary.cache.DiskCache
import com.oliver.imagelibrary.cache.MemoryCache
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class CacheRepositoryTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var memoryCache: MemoryCache

    @Mock
    private lateinit var diskCache: DiskCache

    @Mock
    private lateinit var bitmap: Bitmap

    private lateinit var cacheRepository: CacheRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        cacheRepository = CacheRepository(context).apply {
            this::class.java.getDeclaredField("memoryCache").apply {
                isAccessible = true
                set(this@apply, memoryCache)
            }

            this::class.java.getDeclaredField("diskCache").apply {
                isAccessible = true
                set(this@apply, diskCache)
            }
        }
    }

    @Test
    fun `test put stores bitmap in both caches`() {
        val url = "https://example.com/image.png"

        cacheRepository.put(url, bitmap)

        verify(memoryCache).put(url, bitmap)
        verify(diskCache).put(url, bitmap)
    }

    @Test
    fun `test get retrieves bitmap from memoryCache`() {
        val url = "https://example.com/image.png"

        `when`(memoryCache.get(url)).thenReturn(bitmap)

        val result = cacheRepository.get(url)

        assertEquals(bitmap, result)
        verify(memoryCache).get(url)
        verifyNoInteractions(diskCache)
    }

    @Test
    fun `test get retrieves bitmap from diskCache if not in memoryCache`() {
        val url = "https://example.com/image.png"

        // Mock memoryCache to return null and diskCache to return the bitmap
        `when`(memoryCache.get(url)).thenReturn(null)
        `when`(diskCache.get(url)).thenReturn(bitmap)

        // Call get
        val result = cacheRepository.get(url)

        // Verify that the bitmap is retrieved from diskCache and added to memoryCache
        assertEquals(bitmap, result)
        verify(memoryCache).get(url)
        verify(diskCache).get(url)
        verify(memoryCache).put(url, bitmap)
    }

    @Test
    fun `test get returns null if not in both caches`() {
        val url = "https://example.com/image.png"

        // Mock both caches to return null
        `when`(memoryCache.get(url)).thenReturn(null)
        `when`(diskCache.get(url)).thenReturn(null)

        // Call get
        val result = cacheRepository.get(url)

        // Verify that null is returned
        assertNull(result)
        verify(memoryCache).get(url)
        verify(diskCache).get(url)
    }

    @Test
    fun `test clear clears both caches`() {
        // Call clear
        cacheRepository.clear()

        // Verify that both memoryCache and diskCache are cleared
        verify(memoryCache).clear()
        verify(diskCache).clear()
    }
}