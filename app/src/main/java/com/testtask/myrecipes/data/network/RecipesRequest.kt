package com.testtask.myrecipes.data.network

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Класс, ответственный за направление запроса и получение ответа от сервера
 * На вход получает готовый URL запрос, возвращает Json ответ в формате String
 */

class RecipesRequest(val errorsProcessor: ErrorsProcessor ) {
    fun makeRequest(addressURL: String): String {
        var httpURLConnection: HttpURLConnection? = null
        var bufferedReader: BufferedReader? = null
        val stringBuffer = StringBuffer()
        try{
            val url = URL(addressURL)
            httpURLConnection = url.openConnection() as HttpURLConnection?
            httpURLConnection?.connect()

            val inputStream = httpURLConnection?.inputStream
            bufferedReader = BufferedReader(InputStreamReader(inputStream))

            // считываем построчно ответ, последовательно перенося строки в StringBuffer
            var line = bufferedReader?.readLine() as String // начинаем с первой, проверяя не пустая ли
            if (line != "")
                while(line != null) {
                    stringBuffer.append(line).append("\n")
                    line = bufferedReader?.readLine() as String
                }
            return stringBuffer as String
        } catch (e: MalformedURLException) {
            errorsProcessor.printError("URL request MalformedURLException: $e")
            e.printStackTrace()
        } catch (e: IOException) {
            errorsProcessor.printError("URL request IOException: $e")
            e.printStackTrace()
        } finally { // по окончании работы
            httpURLConnection?.disconnect() // закрываем соединение
            if (bufferedReader != null) { // закрываем буфер
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                    errorsProcessor.printError("URL request finally IO Exception: $e")
                    e.printStackTrace()
                }
            }
        }
        return "connection error"
    }
}