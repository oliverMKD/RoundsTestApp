MyLibrary

A powerful Android library for efficient image caching and loading, designed to help you manage images in your app with ease. It supports both memory and disk caching, providing fast access to images while saving memory.

Features
- Image Caching: Cache images in memory and disk for fast retrieval.
- Image Loading: Easily load images into ImageView components.
- Cache Management: Simple methods to clear the image cache.
- Coroutines Support: Use Kotlin Coroutines for background tasks like image loading and cache clearing.

Usage
1. Initialize the Library
In your application, you can initialize the library using the LibraryFactory:

val library = LibraryFactory.create(context)

The library will handle caching and image loading automatically.


2. Loading Images
   
To load an image into an ImageView, use the loadImage() method:


library.loadImage(

    url = "https://example.com/image.jpg",
    
    imageView = myImageView,
    
    placeholder = R.drawable.placeholder // Optional placeholder image
    
)


3. Clearing Cache
   
To clear the image cache, call the clearCache() method:


library.clearCache()



4. Coroutine Support

This library uses Kotlin Coroutines to handle background tasks, such as image loading and cache clearing.

The operations are performed on a background thread (using Dispatchers.IO), so they won't block the main UI thread.


5. Cache Repository

   
The library internally manages the cache using a combination of memory and disk caches.

You don't need to configure the cache manually; it's all set up for you.
