package com.testtask.myrecipes.data.storage.image_load_save

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import java.io.File

class ImageLoader(val context: Context, val logger: ToasterAndLogger) {
    fun loadImageByFileName(fileName: String): Drawable? { // проверка и загрузка файла по его имени
        // todo: при сохранении очередной картинки в базе сохраняем путь к ней в инстансе рецепта
        // todo: для загрузки фотки берем адрес из инстанса рецепта

        val filePath = context.filesDir.absolutePath + File.separator + fileName // формируем путь
        Log.i("bugfix - image loader", "loading next photo from: $filePath")
        // todo: в процессе разработки модет изменить путь. нужно выносить эту часть наружу сохр-загрузки
        val file = File(filePath) // создаем файл
        if (file.exists()) { // проверяем, существует ли он
            val bitmap = BitmapFactory.decodeFile(filePath) // декодируем файл в картинку
            return BitmapDrawable(context.resources, bitmap) // создаем из файла Drawable
        } else {
            logger.printLog("image loader: no file: $filePath")
            return null // если файла нет, возвращаем null
        }
    }

    fun loadImageByFileAddress(localAddress: String): Drawable? { // проверка и загрузка файла по его адресу
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