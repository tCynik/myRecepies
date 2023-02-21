package com.testtask.myrecipes.data.network.models

import org.json.JSONArray

/**
 * пустой интерфейс для подмены класса ответа под тестирование
 */
interface ResponseAbstract {
    fun getData(): JSONArray?
}