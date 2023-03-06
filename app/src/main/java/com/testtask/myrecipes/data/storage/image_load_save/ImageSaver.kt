package com.testtask.myrecipes.data.storage.image_load_save

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * класс для сохранения изображения в памяти устройства
 * принимает на вход картинку и её имя, а возвращает путь к файлу.
 * Если сохранение не удалось, вовращает метку "NO_LOCAL_IMAGE_PATTERN"
 */

private const val NO_LOCAL_IMAGE_PATTERN = "EMPTY"

class ImageSaver(val contextActivity: Context, val logger: ToasterAndLogger) {
    fun saveImage(image: Drawable, fileName: String): String {
        val bitmap = (image as? BitmapDrawable)?.bitmap ?: return NO_LOCAL_IMAGE_PATTERN // преобразование Drawable в Bitmap

        val outputStream: OutputStream? // открываем поток для записи в файл
        val file = File(contextActivity.filesDir, fileName) // создаем файл с заданным именем\
        val fileAddress = file.absolutePath // путь для последующего возврата при удачной записи

        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // сжатие битмапа в .png
            outputStream.flush()
            outputStream.close()
            return fileAddress
        } catch (e: IOException) {
            logger.printToast("image saving IOException: $e")
            logger.printLog("Image server: image saving IOException: $e")
        }

        return NO_LOCAL_IMAGE_PATTERN // запись не удалась
    }
}