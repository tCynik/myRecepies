package com.testtask.myrecipes.data.network

import android.graphics.drawable.Drawable
import android.util.Log
import com.testtask.myrecipes.data.interfaces.ImageDownloaderInterface
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.domain.ErrorsProcessor
import kotlinx.coroutines.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class ImageDownloader(val errorsProcessor: ErrorsProcessor, val scope: CoroutineScope): ImageDownloaderInterface {

    override fun downloadPicture (addressURL: String, fileName: String): Drawable? {
        try {
            Log.i("bugfix: imageDownloader", "downloading image from address $addressURL")
            // создаем соединение
            val url = URL(addressURL)
            // устанавливаем соединение URLConnection, затем кастим его в HttpUTLConnection
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.doInput // установка соединения в режим плучения данных
            connection.connectTimeout = TIMEOUT // таймаут соединения

            connection.connect()
            if (connection.responseCode != 200)
                return null
            else {
                val inputStream = connection.inputStream // создаем поток ввода
                return Drawable.createFromStream(inputStream, fileName) // получаем из потока Drawable
            }
        } catch (e: MalformedURLException) {
            errorsProcessor.printError("ImageDownloader: invalid URL: $addressURL, Error = $e")
            Log.i("bugfix:imageDownloader", "connection has an error = $e")

            return null
        } catch (e: IOException) {
            errorsProcessor.printError("ImageDownloader: object was not got. Error = $e")
            Log.i("bugfix:imageDownloader", "connection has an error = $e")

            return null
        } catch (e: SecurityException) {
            errorsProcessor.printError("ImageDownloader: securityException. Error = $e")
            Log.i("bugfix:imageDownloader", "connection has an error = $e")

            return null
        }
    }
}