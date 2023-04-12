package com.testtask.myrecipes.data.storage.image_load_save

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import java.io.File

class ImageLoader(val context: Context, val logger: ToasterAndLogger): ImageLoaderInterface {

    override fun loadImageByFileAddress(localAddress: String): Drawable? { // проверка и загрузка файла по его адресу
        Log.i("bugfix - imageLoader", "loading image from local $localAddress")
        val file = File(localAddress) // создаем файл
        if (file.exists()) { // проверяем, существует ли он
            val bitmap = BitmapFactory.decodeFile(localAddress) // декодируем файл в картинку
            return BitmapDrawable(context.resources, bitmap) // создаем из файла Drawable
        } else {
            logger.printLog("image loader: no file on address $localAddress")
            return null // если файла нет, возвращаем null
        }
    }


}