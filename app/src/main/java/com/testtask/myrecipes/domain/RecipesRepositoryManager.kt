package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.ImageDownloaderInterface
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.data.interfaces.ImageSaverInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.RecipesRemoteRequestMaker
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.data.network.URLMaker
import com.testtask.myrecipes.domain.interfaces.ImageDownloadingCallback
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.RecipesCallbackInterface
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

/**
 * Класс, ответственный за упарвлением получением данных. Он решает, какие запросы направлять
 * на сервер, какиее - в Storage.
 * Содержит ImagesDataDirector, отвечающий за управление загрузкой-сохранением изображений локально, и из сети.
 * запросы в ImagesDataDirector из этого класса направляем асинхронно
 */

class RecipesRepositoryManager(
    private val errorsProcessor: ErrorsProcessor,
    private val constantsURLSet: URLConstantsSet,
    val scope: CoroutineScope,
    private val recipesDataCallbackInterface: RecipesCallbackInterface,
    val recipesStorage: RecipesStorageInterface,
    val imageLoader: ImageLoaderInterface,
    val imageSaver: ImageSaverInterface,
    val imageDownloader: ImageDownloaderInterface,
    val logger: ToasterAndLogger
                        ) {
    private var currentData: SortedMap<String, SingleRecipe>? = null // текущие данные, согласно последнему обновлению

    private var requestMaker: RecipesRemoteRequestMaker? = null // инстанс, отвечающий за формирование запроса

    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = updateViewWithImageCallback(),
        imageLoader = imageLoader,
        imageSager = imageSaver,
        imageDownloader = imageDownloader,
        logger = logger)

    init {
        requestMaker = RecipesRemoteRequestMaker(logger)
    }

    private fun saveRecipesData(currentData: SortedMap<String, SingleRecipe>) {
        Log.i("bugfix: RepoManager", "local image address = ${currentData[currentData.firstKey()]!!.pre_image.localAddress}")
        recipesStorage.saveRecipesData(currentData)
    }

    fun getRecipeFullPicture(recipe: SingleRecipe){
        Log.i("bugfix: RepoManager", "getFullRecipe was called")
        scope.async(Dispatchers.IO) { updateSinglePhoto(recipe, isFull = true) }
    }

    // по получаемому из активити репозиториям отрабатываем варианты загрузки
    fun updateData() {
        /**
         * Порядок обновления книги рецептов:
         * Асинхронно качаем книгу из БД, и пытаемся взять из сети.
         * Приход даты из каждого источника обратаываем по-своему.
         * Приход неналловой даты из БД (наиболее вероятно) обрабатывается в следующем порядке:
         * 1) если даты из сети еще нет, выводим список, показываем фотки
         * 2) Если дата из сети наловая, то же самое, плюс выводим тост
         * 3) если дити из сети есть, сравниваем дату, апдейтим где есть разница, пересохраняем
         * Приход неналловой даты их сети обрабатывается следущим образом:
         * 1) если даты из БД еще нет - выводим список
         * 2) если дата из БД наловая, выводим список, пересохраняем
         * 3) если дити из БД есть, сравниваемю рецепты, где есть разница, апдейтим, выводим, пересохраняем
         */

        var hasLocalResponse = false
        var hasRemoteResponse = false

        var remoteData: SortedMap<String, SingleRecipe>? = null
        var localData: SortedMap<String, SingleRecipe>? = null

        // направляем асинхронный запрос в сеть
        val requestURL = URLMaker(constantsURLSet).makeURLRecipesList() // формируем URL запрос
        scope.launch (Dispatchers.IO) {
            remoteData = requestMaker!!.updateRecipesFromNet(requestURL)
            hasRemoteResponse = true // выставлям флаг того, что ответ от url запроса есть
            if (remoteData != null) { // если ответ не налловый
                if (hasLocalResponse) { // если уже есть ответ ответ из БД
                    if (localData == null) { // если этот ответ от БД пуст, значит, в БД пока нет ничего
                        Log.i("bugfix - recipesRepoManager", "has only net data, no local")
                        saveRecipesData(remoteData!!)
                        showWholeRecipesBook(remoteData!!)
                        updatePhotos(remoteData!!)
                    } else { // ответ из БД не пуст. Нужно сравнить базы
                        val updatedData = ComparatorCombinator().compareAndCombineMaps(
                            comparableMap = localData!!,
                            updaterMap = remoteData!!)//todo: сравниваем, сохраняем разницу, обновляем книгу на экране
                        if (updatedData != null) { // если по результатам сравнения есть отличия (null = отличий нет)
                            Log.i("bugfix - recipesRepoManager", "has net and local data both. That is the different, updating one")
                            saveRecipesData(updatedData)
                            showWholeRecipesBook(updatedData)
                            updatePhotos(remoteData!!)
                        } else
                            Log.i("bugfix - recipesRepoManager", "has net and local data both, and no any difference")

                    }
                } else { // если ответа из БД еще не поступил
                    Log.i("bugfix - recipesRepoManager", "has net data, but has not local yet")
                    showWholeRecipesBook(remoteData!!)
                    //recipesDataCallbackInterface.onGotRecipesData(remoteData!!)
                    updatePhotos(remoteData!!) // выводим что загрузили, идем качать фотки
                }
            } else { // если ответ налловый = доступа к сети нет
                Log.i("bugfix - recipesRepoManager", "has no connection server data")
                if (hasLocalResponse) { // сети нет, есть только локальный ответ
                    if (localData != null) {// если при этом локальные данные налловые,
                        logger.printToast("No data")// todo: (выводим заглушку?)
                    } else { // но если данные неналловые,
                        logger.printToast("Network connection error") // просто пишем тост что нет доступа к сети
                    }
                } else { // данные из сети налловые, а данных из локальной БД еще нет
                    logger.printToast("Network connection error")
                }
            }
        }

        // направляем асинхронный запрос на чтение БД
        scope.launch (Dispatchers.IO) {
            val loadedData = recipesStorage.loadRecipesData() // грузим рецепты из БД
            hasLocalResponse = true
            if (loadedData != null) { // если есть неналловый результат из локальной базы
                localData = loadedData
                if (hasRemoteResponse) { // если ранее получили результат из сети
                    Log.i("bugfix - recipesRepoManager", "has local data, has remote one")
                    val updatedData = ComparatorCombinator().compareAndCombineMaps(
                        comparableMap = localData!!,
                        updaterMap = remoteData!!)//todo: сравниваем, сохраняем разницу, обновляем книгу на экране
                    if (updatedData != null) { // если по результатам сравнения есть отличия (null = отличий нет)
                        saveRecipesData(updatedData) //только сохраняем разницу
                    }
                } else { // если результата из сети пока нет
                    Log.i("bugfix - recipesRepoManager", "has local data, has no remote one yet")
                    showWholeRecipesBook(loadedData) // выводим что загрузили из БД
                    updatePhotos(loadedData)
                }
            } else { // если результат из локальной базы налловый
                Log.i("bugfix - recipesRepoManager", "has local data is null")

                if (hasRemoteResponse) { // если локального результата нет, но есть результат с сервера
                    if (remoteData != null) { // если при этом результат из сети неналловый
                        saveRecipesData(remoteData!!)// сохраняем его в базу
                    } else { // либо если налл,
                        logger.printToast("No data") //todo: если результат из сети налл, пишем ошику, либо ставим заглушку
                    }
                } else { // если локальный результат налл, а из сети ответа пока еще нет, делаем ничего
                    logger.printLog("Database is empty. Awaiting network result...")
                }
            }
        }
    }

    private fun updateViewWithImageCallback(): ImageDownloadingCallback {
        return object: ImageDownloadingCallback { // коллбек для обновления даты при получении изображения.
            override fun updateRecipeItemAndSave(recipe: SingleRecipe) {
                // todo: каждый раз вызывает notifyDataSetChanged() - оптимизировать на notifyItemSetChanged()
                Log.i("bugfix: repository manager", "updating & saving. full picture local address = ${recipe.full_image.localAddress}, pre = ${recipe.pre_image.localAddress}")
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
                recipesStorage.saveSingleRecipe(recipe)
                showWholeRecipesBook(currentData!!)
            }

            override fun updateRecipeItemNoSave(recipe: SingleRecipe) {
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
                showWholeRecipesBook(currentData!!)
            }
        }
    }

    private fun updatePhotos(recipesData: SortedMap<String, SingleRecipe>){
        val iterator = recipesData.iterator()
        while (iterator.hasNext()) {
            scope.async (Dispatchers.IO) {updateSinglePhoto(recipe = iterator.next().value, isFull = false)}
        }
    }

    private fun updateSinglePhoto(recipe: SingleRecipe, isFull: Boolean) {
        imagesDataDirector.getImage(recipe = recipe, isFull = isFull)
    }

    private fun showWholeRecipesBook(recipesData: SortedMap<String, SingleRecipe>) {
        currentData = recipesData
        recipesDataCallbackInterface.onGotRecipesData(recipesData) // выводим что загрузили
    }
}