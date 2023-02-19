package com.testtask.myrecipes.data.network

import android.util.Log
import com.testtask.myrecipes.domain.ErrorsProcessor
import com.testtask.myrecipes.domain.interfaces.ResponseResultBacallInterface
import com.testtask.myrecipes.domain.models.SingleRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * класс асинхронно создает запрос и парсит ответ
 */

class NetRecipesListDirector(
    val resultInterface: ResponseResultBacallInterface,
    val errorsProcessor: ErrorsProcessor,
    val scope: CoroutineScope,) {

    val parser = ParserJson()
    val requestMaker = RecipesRequestMaker(errorsProcessor, scope)

    fun makeRequestForList(addressURL: String) {
        var resultRecipesList = listOf<SingleRecipe>()
        scope.launch {
            withContext(Dispatchers.Default) {
                val jsonArray = requestMaker.asyncUpdating(addressURL)
                resultRecipesList = parser.parseJson(jsonArray!!)
                resultInterface.onGetResult(resultRecipesList)
                if (resultRecipesList.isEmpty()) Log.i("bugfix: NetDirector", "result is empty")
                Log.i("bugfix: NetDirector", "result[0] - ${resultRecipesList[0].headline}")
            }
        }
        Log.i("bugfix: recipesRequestRepo", "json in MyAsync = $resultRecipesList")

    }
}