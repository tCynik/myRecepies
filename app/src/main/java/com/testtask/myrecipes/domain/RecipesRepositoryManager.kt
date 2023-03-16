package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.*
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.network.models.ResponseJsonArray
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.interfaces.ImageDownloadingCallback
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.RecipesCallbackInterface
import kotlinx.coroutines.*
import org.json.JSONArray
import java.util.*

/**
 * Класс, ответственный за упарвлением получением данных. Он решает, какие запросы направлять
 * на сервер, какиее - в Storage.
 */

class RecipesRepositoryManager(
    private val errorsProcessor: ErrorsProcessor,
    private val constantsURLSet: URLConstantsSet,
    val scope: CoroutineScope,
    private val recipesDataCallbackInterface: RecipesCallbackInterface,
    val imageLoader: ImageLoader, // todo: reset with interface!
    val imageSaver: ImageSaver, // todo: reset with interface!
    val imageDownloader: ImageDownloader // todo: reset with interface!
                        ) {
    private var currentData: SortedMap<String, SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRequestMaker? = null // инстанс, отвечающий за формирование запроса

    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = getImageCallback(),
        imageLoader = imageLoader,
        imageSager = imageSaver,
        imageDownloader = imageDownloader)

    init {
        val callbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: SortedMap<String, SingleRecipe>? = null
            override fun onHasResponse(jSonData: JSONArray) { // при получении ответа
                resultData = parser.parseJson(ResponseJsonArray(jSonData)) // парсим ответ в формат List<SingleRecipe>
                if (resultData == null) noNetData() // если ответа нет
                else { // если ответ есть
                    if (currentData == null) {
                        currentData = resultData // сохраняем значения в инстнс сессии
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!) // отправляем во ВМ коллбек с результатом
                    }
                    Log.i("bugfix: RepoManager", "got result size: ${currentData!!.size}")

                    val iterator = currentData!!.iterator()
                    while (iterator.hasNext()) {
                        scope.async (Dispatchers.IO) {imagesDataDirector.getImage(recipe = iterator.next().value, isFull = false)}
                    }
                }
            }
        }
        // инстанс, отвечающий за URL запрос, в конструктор получает реализацию интерфейса для коллбека
        requestMaker = RecipesRequestMaker(errorsProcessor, scope, callbackInterface)
    }

    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData(): List<SingleRecipe> {
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
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
                recipesDataCallbackInterface.onGotRecipesData(currentData!!)
            }
        }
    }

    private fun noNetData(){// вызывается для отбаботки ошибок и отправки сообщений
        Log.i("bugfix: recipesRepoManager", "result data is null")
    }

}