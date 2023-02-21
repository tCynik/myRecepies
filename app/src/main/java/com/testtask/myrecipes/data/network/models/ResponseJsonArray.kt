package com.testtask.myrecipes.data.network.models

import org.json.JSONArray

/**
 * класс-обертка для возможности подмены парсера по тестирование
 */

data class ResponseJsonArray(val jsonArray: JSONArray): ResponseAbstract {
    override fun getData(): JSONArray? {
        return jsonArray
    }
}