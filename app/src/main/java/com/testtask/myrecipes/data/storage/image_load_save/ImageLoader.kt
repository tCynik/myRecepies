package com.testtask.myrecipes.data.storage.image_load_save

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File

class ImageLoader(val context: Context) {
    fun loadImage(fileName: String): Drawable? { // проверяем наличие файла и зграаем его
        val filePath = context.filesDir.absolutePath + File.separator + fileName // формируем путь
        // todo: в процессе разработки модет изменитьс путь. нужно выносить эту часть наружу сохр-загрузки
        val file = File(filePath) // создаем файл
        if (file.exists()) { // проверяем, существует ли он
            val bitmap = BitmapFactory.decodeFile(filePath) // декодируем файл в картинку
            return BitmapDrawable(context.resources, bitmap) // создаем из файла Drawable
        } else return null // если файла нет, возвращаем null
    }

}