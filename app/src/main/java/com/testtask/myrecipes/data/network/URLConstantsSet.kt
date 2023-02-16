package com.testtask.myrecipes.data.network

/**
 * класс, содержащий сведения для формирования ЮРЛ запроса. Инстанс создается в активити исходя из
 * R.values.strings и инжектит его при необходимости запроса
 */
class URLConstantsSet(val baseURL: String, val recipesList: String) {
}