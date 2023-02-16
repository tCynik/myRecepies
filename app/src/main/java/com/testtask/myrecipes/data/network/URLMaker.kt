package com.testtask.myrecipes.data.network

/**
 * Класс для составления ЮРЛ.
 * В конструктор принимает мапу с элементами адреса <String, String>,
 * где ключ = тип элемента (базовый ЮРЛ, индекс запрос, и т.д.),
 * а значение = его значение для вставки в адрес
 */

const val KEY_BASE = "baseURL" // ключ вынесен в константу для исключения опечаток
const val ERROR = "error" // на случай если поступит инвалидная мапа без ЮРЛ

class URLMaker(private val addresses: Map<String, String>) {
    fun makeURL(requestType: String): String {
        val baseURL = getBaseURL()
        val request = getRequest(requestType)
        return baseURL.plus(request)
    }

    // получаем из мапы базовый ЮРЛ
    private fun getBaseURL(): String? {
        return if (addresses.containsKey(KEY_BASE))
            addresses[KEY_BASE]
        else ERROR
    }

    // получаем конкретную транскрипцию ЮРЛ запроса
    private fun getRequest(type: String): String? {
        return if (addresses.containsKey(type))
            addresses[type]
        else ERROR
    }
}