package com.example.project.data

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

fun saveImage(context: Context, filename: String, bmp: Bitmap): Boolean {
    return try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
            if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                throw IOException("error evan")
            }
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

fun loadImage(context: Context, filename: String): Bitmap? {
    return try {
        val bytes = context.openFileInput(filename).readBytes()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: FileNotFoundException) {
        // file does not exist
        e.printStackTrace()
        null
    } catch (e: IOException) {
        // other IO exceptions
        e.printStackTrace()
        null
    }
}

fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    try {
        inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}