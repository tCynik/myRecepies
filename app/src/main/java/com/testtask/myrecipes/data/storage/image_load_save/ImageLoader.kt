package com.testtask.myrecipes.data.storage.image_load_save

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import java.io.File

class ImageLoader(val context: Context, val logger: ToasterAndLogger) {
    fun loadImageByFileName(fileName: String): Drawable? { // проверка и загрузка файла по его имени
        val filePath = context.filesDir.absolutePath + File.separator + fileName // формируем путь
        // todo: в процессе разработки модет изменить путь. нужно выносить эту часть наружу сохр-загрузки
        val file = File(filePath) // создаем файл
        if (file.exists()) { // проверяем, существует ли он
            val bitmap = BitmapFactory.decodeFile(filePath) // декодируем файл в картинку
            return BitmapDrawable(context.resources, bitmap) // создаем из файла Drawable
        } else {
            logger.printLog("image loader: no file")
            return null // если файла нет, возвращаем null
        }
    }

    fun loadImageByFileAddress(fileName: String): Drawable? { // проверка и загрузка файла по его адресу
        val file = File(fileName) // создаем файл
        if (file.exists()) { // проверяем, существует ли он
            val bitmap = BitmapFactory.decodeFile(fileName) // декодируем файл в картинку
            return BitmapDrawable(context.resources, bitmap) // создаем из файла Drawable
        } else {
            logger.printLog("image loader: no file")
            return null // если файла нет, возвращаем null
        }
    }


}