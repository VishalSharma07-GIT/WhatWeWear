package com.vishalsharma.whatwewear.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private val bitmapCache = LruCache<String, Bitmap>(30)

private fun decodeDataUri(uri: String): Bitmap? {
    bitmapCache.get(uri)?.let { return it }
    return try {
        val bytes = Base64.decode(uri.substringAfter("base64,"), Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also { bitmapCache.put(uri, it) }
    } catch (e: Exception) {
        null
    }
}

// Use this everywhere an image is shown
@Composable
fun rememberImageModel(uri: String?): Any? = remember(uri) {
    if (uri != null && uri.startsWith("data:image")) decodeDataUri(uri) else uri
}