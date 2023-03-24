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
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
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
    val imageDownloader: ImageDownloader, // todo: reset with interface!
    val logger: ToasterAndLogger
                        ) {
    private var currentData: SortedMap<String, SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRequestMaker? = null // инстанс, отвечающий за формирование запроса

    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = updateViewWithImageCallback(),
        imageLoader = imageLoader,
        imageSager = imageSaver,
        imageDownloader = imageDownloader,
        logger = logger)

    init {
        val recipesNetCallbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: SortedMap<String, SingleRecipe>? = null
            override fun hasNetRecipesResponse(jSonData: JSONArray?) { // при получении ответа
                if (jSonData == null) {
                    currentData = recipesStorage.loadRecipesData() // если в коллбеке ответа нет, грузим из БД
                }
                else { // если ответ есть, грузим из сети
                    resultData = parser.parseJson(ResponseJsonArray(jSonData!!)) // парсим ответ в формат List<SingleRecipe>
                    if (currentData == null) { // если в эту сессию это у нас первый результат
                        currentData = resultData // сохраняем значения в инстанс сессии
                        saveRecipesData(resultData!!)
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!) // отправляем во ВМ коллбек с результатом
                    }
                    logger.printToast("Data downloaded from server")
                    Log.i("bugfix: RepoManager", "got response with size: ${currentData!!.size}")

                }

                if (currentData == null) noNetRecipesData()
                else updatePhotos()
            }
        }
        // инстанс, отвечающий за URL запрос, в конструктор получает реализацию интерфейса для коллбека
        requestMaker = RecipesRequestMaker(logger, scope, recipesNetCallbackInterface)
    }

    private fun saveRecipesData(currentData: SortedMap<String, SingleRecipe>) {
        Log.i("bugfix: RepoManager", "saving downloaded recipes data.")
        Log.i("bugfix: RepoManager", "local image address = ${currentData[currentData.firstKey()]!!.pre_image.localAddress}")
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

//    private fun updateDataFromStorage(): SortedMap<String, SingleRecipe>?{
//        return recipesStorage.loadRecipesData()
//    }

    private fun updateViewWithImageCallback(): ImageDownloadingCallback {
        return object: ImageDownloadingCallback { // коллбек для обновления даты при получении изображения.
            override fun updateRecipeItem(recipe: SingleRecipe) {
                // todo: каждый раз вызывает notifyDataSetChanged() - оптимизировать на notifyItemSetChanged()
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
                saveRecipesData(currentData!!)
                recipesDataCallbackInterface.onGotRecipesData(currentData!!)
            }
        }
    }

    private fun noNetRecipesData(){// действия при невозможности получть дату с сервера
        Log.i("bugfix: recipesRepoManager", "result internet data is empty. Trying to update from storage")// make toast
        val resultData = recipesStorage.loadRecipesData() // если нет ответа из удаленной базы
        if (resultData == null) noLocalRecipesData()
        else {
            logger.printToast("Data was loaded from cash")
            recipesDataCallbackInterface.onGotRecipesData(resultData)
        }
    }

    private fun noLocalRecipesData(){// действия при невозможности получть дату
        Log.i("bugfix: recipesRepoManager", "result local data is empty")
        logger.printToast("no any data!")
    // make toast
    }

    private fun updatePhotos(){
        val iterator = currentData!!.iterator()
        while (iterator.hasNext()) {
            scope.async (Dispatchers.IO) {imagesDataDirector.getImage(recipe = iterator.next().value, isFull = false)}
        }
    }
}