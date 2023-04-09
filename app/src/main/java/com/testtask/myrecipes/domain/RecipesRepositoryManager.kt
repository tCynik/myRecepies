package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.data.interfaces.*
import com.testtask.myrecipes.data.network.*
import com.testtask.myrecipes.data.network.models.ResponseJsonArray
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

    private val parser = ParserJson() // парсинг ответа из JSONArray
    private var requestMaker: RecipesRemoteRequestMaker? = null // инстанс, отвечающий за формирование запроса

    private val imagesDataDirector = ImagesDataDirector(
        imageCallback = updateViewWithImageCallback(),
        imageLoader = imageLoader,
        imageSager = imageSaver,
        imageDownloader = imageDownloader,
        logger = logger)

    init { // тут мы создаем инстанс запроса в сеть о рецептах. Короутина вызывается внутри класса, поэтому результат возвращается через коллбек. Черезжопно, но оставлено для примера возможного решения
        val recipesNetCallbackInterface = object : RecipesNetRepositoryInterface { // коллбек для возврата результата при его получении
            var resultData: SortedMap<String, SingleRecipe>? = null
            override fun hasNetRecipesResponse(jSonData: JSONArray?) { // при получении ответа
                if (jSonData == null) { // ответ пустой, т.е. не получили книгу рецептов
                    logger.printToast("Loading from server error. Loading from storage...")
                    currentData = recipesStorage.loadRecipesData() // грузим рецепты из БД
                    recipesDataCallbackInterface.onGotRecipesData(currentData!!) // отправляем во ВМ коллбек с результатом
                }
                else { // если ответ есть, расшифровываем полученное из сети
                    resultData = parser.parseJson(ResponseJsonArray(jSonData!!)) // парсим ответ в формат List<SingleRecipe>
                    if (currentData == null) { // если в эту сессию это у нас первый результат
                        currentData = resultData // сохраняем значения в инстанс сессии
                        Log.i("bugfix: RepoManager", "saving downloaded recipes data.")
                        saveRecipesData(resultData!!) // сохраняем новую БД рецептов
                        recipesDataCallbackInterface.onGotRecipesData(resultData!!) // отправляем во ВМ коллбек с результатом
                    }
                    logger.printToast("Data downloaded from server")
                    Log.i("bugfix: RepoManager", "got response with size: ${currentData!!.size}")

                }

                if (currentData == null) noNetRecipesData() //todo: double local data loading calling!
                // todo: вызов загрузки из памяти идет дважды - и строчкой выше, и в рамках  обработки jsonData == null в блоке кода наверху

                else updatePhotos()
            }
        }
        // инстанс, отвечающий за URL запрос, в конструктор получает реализацию интерфейса для коллбека
        requestMaker = RecipesRemoteRequestMaker(logger, scope, recipesNetCallbackInterface)
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
    fun updateData(): List<SingleRecipe> {
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

        // направляем асинхронный запрос в сеть
        val requestURL = URLMaker(constantsURLSet).makeURLRecipesList() // формируем запрос
        scope.launch (Dispatchers.IO) {
            remoteData = requestMaker!!.updateRecipesFromNet(requestURL)
            hasRemoteResponse = true // выставлям флаг того, что ответ от запроса есть
            if (remoteData != null) { // если ответ не налловый
                if (hasLocalResponse) { // если уже есть ответ ответ из БД
                    // сравниваем, сохраняем разницу, обновляем книгу на экране
                } else { // если ответа из БД еще нет
                    // выводим что загрузили
                }
            } else { // если ответ налловый = доступа к сети нет
                if (hasLocalResponse) { // сети нет, есть только локальный ответ
                    // если при этом локальные данные налловые, пишем что ошибка (выводим заглушку?)
                    // но если данные неналловые, просто пишем тост что нет доступа к сети
                } else { // данные из сети налловые, а данных из локальной БД еще нет
                    // просто пишем тост что нет доступа к сети
                }
            }
        }

        val localData: List<SingleRecipe>? = null

        // направляем асинхронный запрос на чтение БД
        scope.launch (Dispatchers.IO) {
            val loadedData = recipesStorage.loadRecipesData() // грузим рецепты из БД
            hasLocalResponse = true
            if (loadedData != null) { // если есть неналловый результат из локальной базы
                if (hasRemoteResponse) { // если ранее получили результат из сети
                    // сравниваем, и только сохраняем разницу
                } else { // если результата из сети пока нет
                    // выводим что загрузили
                }
            } else { // если результат из локальной базы налловый
                if (hasRemoteResponse) { // если локального результата нет, но есть результат с сервера
                    // если при этом результат и сети неналловый, сохраняем его в базу
                    // если результат из сети налл, пишем ошику, либо ставим заглушку
                } else { // если нет локального результата и результата с сервера
                    // сохраняем результат из сервера в локальную базу
                }
            }

        }


        // todo: сначала обращаемся в инет за информацией. Если нет интернета, выводим тост о том, что коннекта нема
        //  тянемся в репозиторий, достаем оттуда. Если загрузили -тост что инфо из кэша
        //  если кэша нет - пишем что ошибка загрузки или типтаво.
//        Log.i("bugfix: RepoManager", "updating the data..")

        // создание и обработка запроса данных из сети
        //val requestURL = URLMaker(constantsURLSet).makeURLRecipesList() // формируем запрос
//        requestMaker!!.makeAsyncRequest(requestURL)

        return listOf()//resultData
    }

    private fun updateViewWithImageCallback(): ImageDownloadingCallback {
        return object: ImageDownloadingCallback { // коллбек для обновления даты при получении изображения.
            override fun updateRecipeItemAndSave(recipe: SingleRecipe) {
                // todo: каждый раз вызывает notifyDataSetChanged() - оптимизировать на notifyItemSetChanged()
                Log.i("bugfix: repository manager", "updating & saving. full picture local address = ${recipe.full_image.localAddress}")
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
                recipesStorage.saveSingleRecipe(recipe)
                recipesDataCallbackInterface.onGotRecipesData(currentData!!)
            }

            override fun updateRecipeItemNoSave(recipe: SingleRecipe) {
                if (currentData!!.containsKey(recipe.id))
                    currentData!![recipe.id] = recipe
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
            //scope.async (Dispatchers.IO) {imagesDataDirector.getImage(recipe = iterator.next().value, isFull = false)}
            scope.async (Dispatchers.IO) {updateSinglePhoto(recipe = iterator.next().value, isFull = false)}
        }
    }

    private fun updateSinglePhoto(recipe: SingleRecipe, isFull: Boolean) {
        imagesDataDirector.getImage(recipe = recipe, isFull = isFull)
    }
}