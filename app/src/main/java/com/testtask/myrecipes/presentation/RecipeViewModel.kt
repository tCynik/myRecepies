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
            currentRecipeLive.value = currentRecipe
            Log.i("bugfix - ViewModel", "choosen recipe with id ${currentRecipeLive.value!!.id}")
            repositoryManager!!.getRecipeFullPicture(currentRecipe)
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


        val recipe = recipesDataLive.value?.get(position)

        // запускаем фрагмент с данными
        val picture = recipe!!.pre_image.image
        val name = recipe.name
        //fragmentRecipesCallback!!.setRecipeScreen(picture!!, name)
        fragmentRecipesCallback!!.setRecipeIntoFragmentByNumber(position)

        // запрашиваем обновление картинки
        //repositoryManager!!.getRecipeFullPicture(recipe)
    }

    private fun checkAndUpdateFullPicture(data: SortedMap<String, SingleRecipe>) {
        if (currentRecipeLive.value != null) {// если у нас есть рецепт для отображения во фрагменте
            val currentRecipeId = currentRecipeLive.value!!.id
            val recipe = data[currentRecipeId]
            val fullImage = recipe!!.full_image.image
            if (fullImage != null) {
                Log.i("bugfix - recipeViewModel", "updating the current recipe by full image")
                currentRecipeLive.value = recipe
                // todo: обновление на полную картинку происходит не по сигналу с ливдаты, а только при запуске фрагмента.
                // todo: возможно, стоит обсервить ливдату из мэйнактивити, и уже оттуда вести обновление фрагмента?

                // todo: при первоначальной загрузке фрагмента выдает, что полной картинки нет. Хотя ранее мы ее качали
                // todo: Возможно, проблема в том, что логика менеджера предполагает сначала качать из инета, а потом лезть в память если не вышло?
            }
        }

    }
}