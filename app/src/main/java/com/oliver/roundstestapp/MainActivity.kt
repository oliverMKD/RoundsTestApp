package com.oliver.roundstestapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.oliver.imagelibrary.Library
import com.oliver.imagelibrary.LibraryFactory

class MainActivity : ComponentActivity() {

    private lateinit var library: Library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        library = LibraryFactory.create(this)

        val images = loadImagesFromJson(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ImageAdapter(images, library)
    }

    private fun loadImagesFromJson(
        context: Context,
        fileName: String = "images.json"
    ): List<ImageData> {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonString, Array<ImageData>::class.java).toList()
    }
}
