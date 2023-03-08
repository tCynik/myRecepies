package com.testtask.myrecipes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.ErrorsProcessor
import com.testtask.myrecipes.domain.RecipesRepositoryManager
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.RecipesCallbackInterface

const val BASE_URL = "https://hf-android-app.s3-eu-west-1.amazonaws.com/android-test/"
const val RECIPES_LIST = "recipes.json"

class RecipeViewModel: ViewModel() {
    private val recipesDataLive: MutableLiveData<List<SingleRecipe>> = MutableLiveData() // дата основных данных (рецепты)
    val publicDataLive:  LiveData<List<SingleRecipe>> = recipesDataLive // ливдата для работы обсервера активити

    val errorProcessor = ErrorsProcessor() // для вывода ошибок на UI
    val constantsURLSet = URLConstantsSet(baseURL = BASE_URL, recipesList = RECIPES_LIST) // данные для формирования запроса из предсхраненных оций
    var repositoryManager: RecipesRepositoryManager? = null // инстанс класса, отвечающий со всеми отношениями с данынми, памятью, сетью, и т.д.
    var imageLoader: ImageLoader? = null

    init {
//        val recipesDataCallbackInterface = object: RecipesCallbackInterface{ // реализация интерфейса коллбека результатов
//            override fun onGotRecipesData(data: List<SingleRecipe>) {
//                // обновляем UI в главном потоке
//                recipesDataLive.postValue(data)
//            }
//        }
//        // создаем инстанс менеджера с учетом реализации интерфейса коллбека
//        repositoryManager = RecipesRepositoryManager(
//            errorsProcessor = errorProcessor,
//            constantsURLSet = constantsURLSet,
//            scope = viewModelScope,
//            recipesDataCallbackInterface = recipesDataCallbackInterface,
//            imageDownloader =,
//            imageLoader = ,
//            imageSaver =  )
    }

    fun initRepositoryManager( // передача зависимостей. Производится либо после запуска приложения, либо вручную при тестировании
        imageDownloader: ImageDownloader,
        imageLoader: ImageLoader,
        imageSaver: ImageSaver) {
        val recipesDataCallbackInterface = object: RecipesCallbackInterface{ // реализация интерфейса коллбека результатов
            override fun onGotRecipesData(data: List<SingleRecipe>) {
                // обновляем UI в главном потоке
                recipesDataLive.postValue(data)
            }
        }

        repositoryManager = RecipesRepositoryManager(
            errorsProcessor = errorProcessor,
            constantsURLSet = constantsURLSet,
            scope = viewModelScope,
            recipesDataCallbackInterface = recipesDataCallbackInterface,
            imageDownloader = imageDownloader,
            imageLoader = imageLoader,
            imageSaver =  imageSaver)

    }

    fun updateDataWhenActivityCreated(repositoryStorage: RecipesStorageInterface) {
        if (recipesDataLive.value!!.isEmpty())
            repositoryManager?.updateData(repositoryFromStorage = repositoryStorage)
    }
}