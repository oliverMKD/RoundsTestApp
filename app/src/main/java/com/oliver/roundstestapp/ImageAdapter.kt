package com.oliver.roundstestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.oliver.imagelibrary.Library

class ImageAdapter(
    private val images: List<ImageData>,
    private val library: Library
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_view, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = images[position]
        library.loadImage(
            url = imageData.imageUrl,
            imageView = holder.imageView,
            placeholder = R.drawable.placeholder_image
        )
    }

    override fun getItemCount(): Int = images.size
}
