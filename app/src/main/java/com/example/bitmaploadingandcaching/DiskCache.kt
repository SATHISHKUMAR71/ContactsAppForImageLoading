package com.example.bitmaploadingandcaching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object DiskCache {
    private const val CACHE_DIR = "bitmap_cache"

    fun saveBitMap(context: Context,key:String,bitmap: Bitmap){
        val file = File(context.cacheDir, CACHE_DIR)
        if(!file.exists()){
            file.mkdirs()
        }
        if(loadBitMap(context,key)==null){
            val bitmapFile = File(file,key)
            val outputStream = FileOutputStream(bitmapFile)
            println("Data Stored Successfully")
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        }
    }

    fun loadBitMap(context: Context,key: String):Bitmap?{
        val file = File(context.cacheDir, CACHE_DIR)
        val bitmapFile = File(file,key)
        return if(bitmapFile.exists()){
            println("Data Loaded Successfully")
            BitmapFactory.decodeFile(bitmapFile.absolutePath)
        } else{
            println("Data Loaded failed")
            null
        }
    }
}