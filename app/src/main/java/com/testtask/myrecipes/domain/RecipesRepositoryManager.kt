package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.*
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.RecipesCallbackInterface
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray

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
    private val scope: CoroutineScope,
    private val recipesDataCallbackInterface: RecipesCallbackInterface
                        ) {
    private var currentData: List<SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRequestMaker? = null // инстанс, отвечающий за формирование запроса
    init {
        val callbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: List<SingleRecipe>? = null
            override fun onHasResponse(jSonData: JSONArray) {
                resultData = parser.parseJson(jSonData!!) // парсим ответ в формат List<SingleRecipe>
                if (resultData == null) noNetData()
                else {
                    if (currentData == null) {
                        currentData = resultData
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!)
                    }
                    Log.i("bugfix: recipesRequestRepo", "result[0] - ${resultData!![0].headline}")
                }
            }
        }
        requestMaker = RecipesRequestMaker(errorsProcessor, scope, callbackInterface)
    }


    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData(
        repositoryFromStorage: RecipesStorageInterface // для работы с контекстом передаем сюда storage для загрузки из памяти
    ): List<SingleRecipe> {
        // todo: сначала обращаемся к storage и качаем дату из памяти потом обращаемся в интернет.
        //  Когда приходит ответ из сети и если он отличается от информации из памяти, обновляем даныне


        // создание и обработка запроса данных из сети
        val requestURL = URLMaker(constantsURLSet).makeURLRecipesList() // формируем запрос
        requestMaker!!.myAsyncRequest(requestURL)

        return listOf()//resultData
    }

    private fun updateDataFromStorage(repository: RecipesStorageInterface){

    }

    private fun noNetData(){// вызывается для отбаботки ошибок и отправки сообщений

    }

}