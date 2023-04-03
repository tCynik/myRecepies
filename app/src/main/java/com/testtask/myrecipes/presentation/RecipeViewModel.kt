package com.testtask.myrecipes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testtask.myrecipes.MainActivity
import com.testtask.myrecipes.data.interfaces.ImageDownloaderInterface
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.data.interfaces.ImageSaverInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.data.storage.RecipesStorage
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.ErrorsProcessor
import com.testtask.myrecipes.domain.RecipesRepositoryManager
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.RecipesCallbackInterface
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import java.util.*

const val BASE_URL = "https://hf-android-app.s3-eu-west-1.amazonaws.com/android-test/"
const val RECIPES_LIST = "recipes.json"

class RecipeViewModel: ViewModel() {
    private val recipesDataLive: MutableLiveData<List<SingleRecipe>> = MutableLiveData() // дата основных данных (рецепты)
    val publicDataLive:  LiveData<List<SingleRecipe>> = recipesDataLive // ливдата для работы обсервера активити

    val errorProcessor = ErrorsProcessor() // для вывода ошибок на UI
    val constantsURLSet = URLConstantsSet(baseURL = BASE_URL, recipesList = RECIPES_LIST) // данные для формирования запроса из предсхраненных оций
    var repositoryManager: RecipesRepositoryManager? = null // инстанс класса, отвечающий со всеми отношениями с данынми, памятью, сетью, и т.д.

    var fragmentRecipesCallback: MainActivity.RecipeFragmentCallback? = null

    fun initRepositoryManager( // передача зависимостей. Производится либо после запуска приложения, либо вручную при тестировании
        imageDownloader: ImageDownloaderInterface,
        imageLoader: ImageLoaderInterface,
        imageSaver: ImageSaverInterface,
        recipesStorage: RecipesStorageInterface,
        logger: ToasterAndLogger) {
        val recipesDataCallbackInterface = object: RecipesCallbackInterface{ // реализация интерфейса коллбека результатов
            override fun onGotRecipesData(data: SortedMap<String, SingleRecipe>) {
                // обновляем UI в главном потоке

                val listData: List<SingleRecipe> = data.values.toList()
                recipesDataLive.postValue(listData)
            }
        }

        repositoryManager = RecipesRepositoryManager(
            errorsProcessor = errorProcessor,
            constantsURLSet = constantsURLSet,
            scope = viewModelScope,
            recipesDataCallbackInterface = recipesDataCallbackInterface,
            recipesStorage = recipesStorage,
            imageDownloader = imageDownloader,
            imageLoader = imageLoader,
            imageSaver =  imageSaver,
            logger = logger)
    }

    fun updateDataWhenActivityCreated() {
        if (recipesDataLive.value == null)
            repositoryManager?.updateData()
    }

    fun setFragmentCallback(callback: MainActivity.RecipeFragmentCallback) {
        this.fragmentRecipesCallback = callback
    }

    fun pictureWasClicked(position: Int) { // получили команду о нажатии на картинку
        val recipe = recipesDataLive.value?.get(position)

        // запускаем фрагмент с данными
        val picture = recipe!!.pre_image.image
        val name = recipe!!.name
        fragmentRecipesCallback!!.setRecipeScreen(picture!!, name)

        // запрашиваем обновление картинки
        repositoryManager!!.getRecipeFullPicture(recipe)
    }
}