package com.testtask.myrecipes.data.network

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.domain.ErrorsProcessor
import kotlinx.coroutines.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.sql.Connection

/**
 * Класс, ответственный за направление запроса и асинхронное получение ответа от сервера
 * На вход получает готовый URL запрос и скоуп, в котором выполняется асинхронный запрос.
 * возвращает jsonArray ответ в формате String (при изменении формата ответа сервера нужно менять код)
 */

const val TIMEOUT = 15000
class RecipesRequestMaker(
    val errorsProcessor: ErrorsProcessor,
    val scope: CoroutineScope,
    val resultInterface: RecipesNetRepositoryInterface
    ) {

    fun myAsyncRequest(url: String): JSONArray? {
        var result: JSONArray? = null
        scope.launch {
            withContext(Dispatchers.Default) {
                result = asyncUpdating(url)
                result?.let { resultInterface.onHasResponse(it) }
            }
        }
        return result
    }


    private fun asyncUpdating(addressURL: String): JSONArray? { // метод асинхронного обращени к серверу
        val url = URL(addressURL)
        val connection: HttpURLConnection
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = TIMEOUT // таймаут соединения

        try{
            connection.connect()
            Log.i("bugfix: recipesRequestRepo", "connected code - ${connection.responseCode}")
            // todo: switch to logger

            if (connection.responseCode == 200)
                return connectionResult(connection)
            else return null

        } catch (e: MalformedURLException) {
            errorsProcessor.printError("URL request MalformedURLException: $e")
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            errorsProcessor.printError("URL request IOException: $e")
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            errorsProcessor.printError("Exception: $e")
            Log.i("bugfix: recipesRequestRepo", "exception $e")
            e.printStackTrace()
            return null
        }
    }

    private fun connectionResult(connection: HttpURLConnection): JSONArray? {
        val response = StringBuilder()
        val inputStream = connection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        var line: String?
        line = bufferedReader.readLine()

        while (line != null) {
            response.append(line)
            line = bufferedReader.readLine()
        }
        return JSONArray(response.toString())
    }
}