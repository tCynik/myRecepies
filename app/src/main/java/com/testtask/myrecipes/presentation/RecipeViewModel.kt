package com.testtask.myrecipes.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testtask.myrecipes.MainActivity
import com.testtask.myrecipes.data.interfaces.ImageDownloaderInterface
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.data.interfaces.ImageSaverInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.URLConstantsSet
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

    private val currentRecipeLive: MutableLiveData<SingleRecipe?> = MutableLiveData()
    val publicCurrentRecipeLive: LiveData<SingleRecipe?> = currentRecipeLive

    val errorProcessor = ErrorsProcessor() // для вывода ошибок на UI
    val constantsURLSet = URLConstantsSet(baseURL = BASE_URL, recipesList = RECIPES_LIST) // данные для формирования запроса из предсхраненных оций
    var repositoryManager: RecipesRepositoryManager? = null // инстанс класса, отвечающий со всеми отношениями с данынми, памятью, сетью, и т.д.

    var fragmentRecipesCallback: MainActivity.RecipeFragmentCallback? = null

    var isNumberSelected = false
    var currentNumber = 0

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
                if (isNumberSelected) currentRecipeLive.postValue(listData[currentNumber])
                // проверяем, не обновлялась ли полная картинка из фрагмента
                checkAndUpdateFullPicture(data)
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

    fun setCurrentNumber(number: Int?) { // управляемое снаружи обновление текущим открытым рецептом
        if (number == null) currentRecipeLive.value = null
        else {
            val currentRecipe = recipesDataLive.value!!.get(number)
            repositoryManager!!.getRecipeFullPicture(currentRecipe)
            isNumberSelected = true
            currentNumber = number
        }
    }

    fun updateDataWhenActivityCreated() {
        if (recipesDataLive.value == null)
            repositoryManager?.updateData()
    }

    fun setFragmentCallback(callback: MainActivity.RecipeFragmentCallback) {
        this.fragmentRecipesCallback = callback
    }

    fun pictureWasClicked(position: Int) { // получили команду о нажатии на картинку
        currentRecipeLive.value = recipesDataLive.value!!.get(position)
        fragmentRecipesCallback!!.setRecipeIntoFragmentByNumber(position)
    }

    private fun checkAndUpdateFullPicture(data: SortedMap<String, SingleRecipe>) {
        if (currentRecipeLive.value != null) {// если у нас есть рецепт для отображения во фрагменте
            val currentRecipeId = currentRecipeLive.value!!.id
            val recipe = data[currentRecipeId]
            val fullImage = recipe!!.full_image.image
            if (fullImage != null) {
                Log.i("bugfix - recipeViewModel", "updating the current recipe by full image")
                currentRecipeLive.value = recipe
            }
        }
    }
}