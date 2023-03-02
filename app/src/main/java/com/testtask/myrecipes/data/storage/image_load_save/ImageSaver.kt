package com.testtask.myrecipes.data.storage.image_load_save

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.testtask.myrecipes.presentation.interfaces.ToasterInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * класс для сохранения изображения в памяти устройства
 */

class ImageSaver(val context: Context, val toaster: ToasterInterface) {
    fun saveImage(image: Drawable, fileName: String): Boolean {
        val bitmap = (image as? BitmapDrawable)?.bitmap ?: return false // преобразование Drawable в Bitmap
        val outputStream: OutputStream? // открываем поток для записи в файл
        val file = File(context.filesDir, fileName) // создаем файл с заданным именем

        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // сжатие битмапа в .png
            outputStream.flush()
            outputStream.close()
            return true
        } catch (e: IOException) {
            toaster.print("image saving IOException: $e")
        }

        return false // запись не удалась
    }
}