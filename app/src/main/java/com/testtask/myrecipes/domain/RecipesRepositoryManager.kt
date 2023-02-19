package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesStorageRepositoryInterface
import com.testtask.myrecipes.data.network.*
import com.testtask.myrecipes.domain.interfaces.ResponseResultBacallInterface
import com.testtask.myrecipes.domain.models.SingleRecipe
import kotlinx.coroutines.CoroutineScope

/**
 * короче, мы либо берем из репозитория, либо (доп.задача) из инета
 **
 * // управление репозиторием - делаем запрос, возвращаем распарсенную дату
 *
 * Класс, ответственный за упарвлением запросами составление URL запроса (в отдельном классе)
 * и направление его на сервер
 * На вход получает готовый URL запрос, возвращает Json ответ в формате String
 */

class RecipesRepositoryManager(
    private val errorsProcessor: ErrorsProcessor,
    private val constantsURLSet: URLConstantsSet,
    private val scope: CoroutineScope
                        ) {
    private val requestMaker = RecipesRequestMaker(errorsProcessor, scope)
    private val parser = ParserJson()


    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData(
        repositoryFromStorage: RecipesStorageRepositoryInterface
    ): List<SingleRecipe> {
        // todo: logic with update method: network or local storage

        // переходим к запросу из сети
        
        val callbackInterface = object : ResponseResultBacallInterface {
            override fun onGetResult(result: List<SingleRecipe>) {

            }
        }
        val netDirector = NetRecipesListDirector()
        val requestString = URLMaker(constantsURLSet).makeURLRecipesList() // формируем запрос
        val responseJsonArray = requestMaker.makeRequest(requestString) // направляем запрос

        val resultData = parser.parseJson(responseJsonArray!!) // парсим ответ в формат List<SingleRecipe>
        Log.i("bugfix: recipesRequestRepo", "result[0] - ${resultData[0].headline}")

        return listOf()//resultData
    }

    private fun updateDataFromStorage(repository: RecipesStorageRepositoryInterface){

    }

    private fun returnAnswer(result)

}