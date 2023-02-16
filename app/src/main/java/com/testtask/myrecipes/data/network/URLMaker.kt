package com.testtask.myrecipes.data.network

/**
 * Класс для составления ЮРЛ.
 * В конструктор принимает класс с константами для формирования запроса
 * конкретный состав запроса орпеделяется выбором методов запроса, для расширения функционала добавить метод
 * если понадобится формировать запрос другого содержания, например обновления рецепта по его id:
 * // к примеру, обновление вызывается запросом: https://hf-android-app.s3-eu-west-1.amazonaws.com/android-test/recipe/id.json,
 * //   где id = подставленный айди записи,
 * в URLConstantsSet добавляется поле val recepieById: String, которому в активити инжектится значение "recipe/"
 * в класс добавляется метод fun makeURLRecepieById(id: String)
 */

class URLMaker(private val constantsURLSet: URLConstantsSet) {
    fun makeURLRecipesList(): String {
        val baseURL = constantsURLSet.baseURL
        val request = constantsURLSet.recipesList
        return baseURL.plus(request)
    }
}