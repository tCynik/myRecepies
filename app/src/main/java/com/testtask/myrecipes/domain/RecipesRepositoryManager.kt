package com.testtask.myrecipes.domain

import android.graphics.drawable.Drawable
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
    private var currentData: MutableList<SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRequestMaker? = null // инстанс, отвечающий за формирование запроса

    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = getImageCallback(),
        imageLoader = imageLoader,
        imageSager = imageSaver,
        imageDownloader = imageDownloader)

    init {
        val callbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: MutableList<SingleRecipe>? = null
            override fun onHasResponse(jSonData: JSONArray) { // при получении ответа
                resultData = parser.parseJson(ResponseJsonArray(jSonData)) // парсим ответ в формат List<SingleRecipe>
                if (resultData == null) noNetData() // если ответа нет
                else { // если ответ есть
                    if (currentData == null) {
                        currentData = resultData // сохраняем значения в инстнс сессии
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!) // отправляем во ВМ коллбек с результатом
                    }
                    Log.i("bugfix: RepoManager", "got result size: ${currentData!!.size}")
                    for (i in 0 until currentData!!.size) {
                        scope.async (Dispatchers.IO) {imagesDataDirector.getImage(recipe = currentData!![i], isFull = false)}
                    }
                }
            }
        }
        // инстанс, отвечающий за URL запрос, в конструктор получает реализацию интерфейса для коллбека
        requestMaker = RecipesRequestMaker(errorsProcessor, scope, callbackInterface)
    }

    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData(repositoryFromStorage: RecipesStorageInterface // для работы с контекстом передаем сюда storage для загрузки из памяти
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
                while (i < currentData!!.size) { // перебираем имеющийся массив даты в поисках рецепта с таким же ID
                    if (recipe.id != currentData!![i].id) {
                        Log.i("bugfix: recipesManager", "setting ${recipe.id} to ${currentData!![i].id} with photo = ${recipe.pre_image.image != null}")
                        currentData!![i] = recipe
                        i++
                    }
                }
                recipesDataCallbackInterface.onGotRecipesData(currentData as List<SingleRecipe>)
            }
        }
    }

    private fun noNetData(){// вызывается для отбаботки ошибок и отправки сообщений
        Log.i("bugfix: recipesRepoManager", "result data is null")
    }

}