package com.testtask.myrecipes.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testtask.myrecipes.data.interfaces.RecipesNetRepositoryInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageRepositoryInterface
import com.testtask.myrecipes.data.models.SingleRecipeData
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.domain.RecipesRepositoryManager
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.ToasterInterface

class RecipeViewModel(private val toaster: ToasterInterface,
                      private val constantsURLSet: URLConstantsSet): ViewModel() {
    val recipesData: MutableLiveData<MutableList<SingleRecipe>> = MutableLiveData()
    val repositoryManager = RecipesRepositoryManager(toaster, constantsURLSet)

//    init {
//        updateDataWhenActivityStarted()
//    }

    fun updateDataWhenActivityStarted(
        repositoryNetwork: RecipesNetRepositoryInterface,
        repositoryStorage: RecipesStorageRepositoryInterface) {
        repositoryManager.updateData(repositoryFromNet = repositoryNetwork, repositoryFromStorage = repositoryStorage)
    }
}