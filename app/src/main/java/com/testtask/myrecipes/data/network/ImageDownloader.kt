package com.testtask.myrecipes.data.network

import android.graphics.drawable.Drawable
import com.testtask.myrecipes.domain.ErrorsProcessor
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class ImageDownloader(val errorsProcessor: ErrorsProcessor) {
    fun downloadPicture (addressURL: String, fileName: String): Drawable? {
        try {
            // создаем соединение
            val url = URL(addressURL)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput
            connection.connect()


            val inputStream = connection.inputStream // создаем поток ввода
            return Drawable.createFromStream(inputStream, fileName) // получаем из потока Drawable

        } catch (e: MalformedURLException) {
            errorsProcessor.printError("ImageDownloader: invalid URL: $addressURL, Error = $e")
            return null
        } catch (e: IOException) {
            errorsProcessor.printError("ImageDownloader: object was not got. Error = $e")
            return null
        } catch (e: SecurityException) {
            errorsProcessor.printError("ImageDownloader: securityException. Error = $e")
            return null
        }
    }
}