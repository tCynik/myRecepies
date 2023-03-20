package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.data.network.*
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.network.models.ResponseJsonArray
import com.testtask.myrecipes.data.storage.RecipesStorage
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
    val recipesStorage: RecipesStorage,
    val imageLoader: ImageLoader, // todo: reset with interface!
    val imageSaver: ImageSaver, // todo: reset with interface!
    val imageDownloader: ImageDownloader // todo: reset with interface!
                        ) {
    private var currentData: SortedMap<String, SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRequestMaker? = null // инстанс, отвечающий за формирование запроса

    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = updateViewWithImageCallback(),
        imageLoader = imageLoader,
        imageSager = imageSaver,
        imageDownloader = imageDownloader)

    init {
        val recipesNetCallbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: SortedMap<String, SingleRecipe>? = null
            override fun onHasResponse(jSonData: JSONArray) { // при получении ответа
                resultData = parser.parseJson(ResponseJsonArray(jSonData)) // парсим ответ в формат List<SingleRecipe>
                if (resultData == null) currentData = recipesStorage.loadRecipesData() // если ответа нет
                else { // если ответ есть
                    if (currentData == null) { // если в эту сессию это у нас первый результат
                        currentData = resultData // сохраняем значения в инстанс сессии
                        saveRecipesData(resultData!!)
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!) // отправляем во ВМ коллбек с результатом
                    }
                    Log.i("bugfix: RepoManager", "got response with size: ${currentData!!.size}")

                }

                if (currentData == null) noNetRecipesData()
                else updatePhotos()
            }
        }
        // инстанс, отвечающий за URL запрос, в конструктор получает реализацию интерфейса для коллбека
        requestMaker = RecipesRequestMaker(errorsProcessor, scope, recipesNetCallbackInterface)
    }

    private fun saveRecipesData(currentData: SortedMap<String, SingleRecipe>) {
        Log.i("bugfix: RepoManager", "updating the data..")
        recipesStorage.saveRecipesData(currentData)
    }

    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData(): List<SingleRecipe> {
        // todo: сначала обращаемся в инет за информацией. Если нет интернета, выводим тост о том, что коннекта нема
        //  тянемся в репозиторий, достаем оттуда. Если загрузили -тост что инфо из кэша
        //  если кэша нет - пишем что ошибка загрузки или типтаво.
        Log.i("bugfix: RepoManager", "updating the data..")

        // создание и обработка запроса данных из сети
        val requestURL = URLMaker(constantsURLSet).makeURLRecipesList() // формируем запрос
        requestMaker!!.makeAsyncRequest(requestURL)

        return listOf()//resultData
    }

    private fun updateDataFromStorage(): SortedMap<String, SingleRecipe>?{
        return recipesStorage.loadRecipesData()
    }

    private fun updateViewWithImageCallback(): ImageDownloadingCallback {
        return object: ImageDownloadingCallback { // коллбек для обновления даты при получении изображения.
            override fun updateRecipeItem(recipe: SingleRecipe) {
                // todo: каждый раз вызывает notifyDataSetChanged() - оптимизировать на notifyItemSetChanged()
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
                recipesDataCallbackInterface.onGotRecipesData(currentData!!)
            }
        }
    }

    private fun noNetRecipesData(){// действия при невозможности получть дату
        Log.i("bugfix: recipesRepoManager", "result internet data is empty. Trying to update from storage")// make toast
        val resultData = recipesStorage.loadRecipesData()
        if (resultData == null) noLocalRecipesData()
        else {
            Log.i("bugfix: recipesRepoManager", "has data from net. updating the view")
            // make toast
            recipesDataCallbackInterface.onGotRecipesData(resultData)
        }
    }

    private fun noLocalRecipesData(){// действия при невозможности получть дату
        Log.i("bugfix: recipesRepoManager", "result local data is empty")
    // make toast
    }

    private fun updatePhotos(){
        val iterator = currentData!!.iterator()
        while (iterator.hasNext()) {
            scope.async (Dispatchers.IO) {imagesDataDirector.getImage(recipe = iterator.next().value, isFull = false)}
        }
    }
}