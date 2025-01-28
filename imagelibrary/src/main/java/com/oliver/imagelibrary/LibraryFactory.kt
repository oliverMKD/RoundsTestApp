package com.oliver.imagelibrary

import android.content.Context

object LibraryFactory {

    private var library: Library? = null

    fun create(context: Context): Library {
        return library ?: synchronized(this) {
            library ?: createLibrary(context).also { library = it }
        }
    }

    private fun createLibrary(context: Context): Library {
        return Library(context.applicationContext)
    }
}