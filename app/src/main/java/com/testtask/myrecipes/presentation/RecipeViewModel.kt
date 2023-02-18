package com.testtask.myrecipes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testtask.myrecipes.R
import com.testtask.myrecipes.data.interfaces.RecipesStorageRepositoryInterface
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.domain.ErrorsProcessor
import com.testtask.myrecipes.domain.RecipesRepositoryManager
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.ToasterInterface

const val BASE_URL = "https://hf-android-app.s3-eu-west-1.amazonaws.com/android-test/"
const val RECIPES_LIST = "recipes.json"
class RecipeViewModel(): ViewModel() {
    val recipesDataLive: LiveData<List<SingleRecipe>> = MutableLiveData()
    val errorProcessor = ErrorsProcessor()
    val constantsURLSet = URLConstantsSet(baseURL = BASE_URL, recipesList = RECIPES_LIST)
    val repositoryManager = RecipesRepositoryManager(errorProcessor, constantsURLSet, viewModelScope)

    init {
        //val scope = ViewModelS
        //updateDataWhenActivityStarted()
    }

    fun updateDataWhenActivityStarted(repositoryStorage: RecipesStorageRepositoryInterface) {
//        val constantsURL = URLConstantsSet(
//            baseURL = getString(R.string.base_url),
//            recipesList = getString(R.string.type_url))

        val updatedData = repositoryManager.updateData(repositoryFromStorage = repositoryStorage)

        //recipesDataLive.value = updatedData
    }
                      }