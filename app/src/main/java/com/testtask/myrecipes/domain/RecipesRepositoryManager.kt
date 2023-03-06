package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.*
import com.testtask.myrecipes.data.network.models.ResponseJsonArray
import com.testtask.myrecipes.domain.interfaces.ImageDownloadingCallback
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.RecipesCallbackInterface
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray

/**
 * Класс, ответственный за упарвлением получением данных. Он решает, какие запросы направлять
 * на сервер, какиее - в Storage.
 */

class RecipesRepositoryManager(
    private val errorsProcessor: ErrorsProcessor,
    private val constantsURLSet: URLConstantsSet,
    private val scope: CoroutineScope,
    private val recipesDataCallbackInterface: RecipesCallbackInterface
                        ) {
    private var currentData: MutableList<SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRequestMaker? = null // инстанс, отвечающий за формирование запроса
    
    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = getImageCallback(),
        scope = scope,
        imageLoader = ,
        imageSager = ,
        imageDownloader = )

    init {
        val callbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: List<SingleRecipe>? = null
            override fun onHasResponse(jSonData: JSONArray) { // при получении ответа
                resultData = parser.parseJson(ResponseJsonArray(jSonData)) // парсим ответ в формат List<SingleRecipe>
                if (resultData == null) noNetData() // если ответа нет
                else { // если ответ есть
                    if (currentData == null) {
                        currentData = resultData // сохраняем значения в инстнс сессии
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!)
                    }
                    currentData!!.forEach { recipe ->
                        imagesDataDirector.getImage(recipe = recipe, isFull = false)
                    }
                }
            }
        }
        // инстанс, отвечающий за URL запрос, в конструктор получает реализацию интерфейса для коллбека
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

    private fun getImageCallback(): ImageDownloadingCallback {
        return object: ImageDownloadingCallback { // коллбек для обновления даты при получении изображения.
            override fun updateRecipeItem(recipe: SingleRecipe) {
                // todo: каждый раз вызывает notifyDataSetChanged() - оптимизировать на notifyItemSetChanged()
                var i = 0
                while (i < currentData!!.size) {
                    if (recipe.id != currentData!![i].id) {
                        currentData!![i] = recipe
                    }
                }
            }
        }
    }

    private fun noNetData(){// вызывается для отбаботки ошибок и отправки сообщений
        Log.i("bugfix: recipesRepoManager", "result data is null")
    }

}