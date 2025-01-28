package com.oliver.roundstestapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.oliver.imagelibrary.Library
import com.oliver.roundstestapp.ui.theme.RoundsTestAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var library: Library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        library = Library.getInstance(this)

        val images = loadImagesFromJson(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns
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
