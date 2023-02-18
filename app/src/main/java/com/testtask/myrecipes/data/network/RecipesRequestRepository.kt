package com.testtask.myrecipes.data.network

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.domain.ErrorsProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Класс, ответственный за направление запроса и асинхронное получение ответа от сервера
 * На вход получает готовый URL запрос и скоуп, в котором выполняется асинхронный запрос.
 * возвращает jsonArray ответ в формате String (при изменении формата ответа сервера нужно менять код)
 */

class RecipesRequestRepository(
    val errorsProcessor: ErrorsProcessor,
    val scope: CoroutineScope,
    ): RecipesNetRepositoryInterface {

    override fun makeRequest(addressURL: String): JSONArray? {
        var resultJson: JSONArray? = null
        Log.i("bugfix: reciresReqestRepo", "ready2 start coroutine")
        scope.launch {  resultJson = asyncUpdating(addressURL) }
        Log.i("bugfix: reciresReqestRepo", "coroutine is ended")

        return resultJson
//        var connection: HttpURLConnection? = null
//        var bufferedReader: BufferedReader? = null
//        val stringBuffer = StringBuffer()
//        try{
//            connection?.connect()
//
//
//            val inputStream = connection?.inputStream
//            bufferedReader = BufferedReader(InputStreamReader(inputStream))
//
//            // считываем построчно ответ, последовательно перенося строки в StringBuffer
//            var line = bufferedReader?.readLine() as String // начинаем с первой, проверяя не пустая ли
//            if (line != "")
//                while(line != null) {
//                    stringBuffer.append(line).append("\n")
//                    line = bufferedReader?.readLine() as String
//                }
//            return stringBuffer as String
//        } catch (e: MalformedURLException) {
//            errorsProcessor.printError("URL request MalformedURLException: $e")
//            e.printStackTrace()
//        } catch (e: IOException) {
//            errorsProcessor.printError("URL request IOException: $e")
//            e.printStackTrace()
//        } finally { // по окончании работы
//            connection?.disconnect() // закрываем соединение
//            if (bufferedReader != null) { // закрываем буфер
//                try {
//                    bufferedReader.close()
//                } catch (e: IOException) {
//                    errorsProcessor.printError("URL request finally IO Exception: $e")
//                    e.printStackTrace()
//                }
//            }
//        }
//        return "connection error"
    }

    fun asyncUpdating(addressURL: String): JSONArray? { // метод асинхронного обращени к серверу
        val url = URL(addressURL)
        val connection: HttpURLConnection
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000 // таймаут соединения
        Log.i("bugfix: reciresReqestRepo", "going 2 make connection on address: $addressURL")


        try{
            connection.connect()
            val inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            val response = StringBuilder()
            var line: String? = null

            while (run {
                    line = bufferedReader.readLine()
                    line
                } != null) {
                response.append(line!!.trim())
            }

            return JSONArray(response.toString())
        } catch (e: MalformedURLException) {
            errorsProcessor.printError("URL request MalformedURLException: $e")
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            errorsProcessor.printError("URL request IOException: $e")
            e.printStackTrace()
            return null
        }
    }
}