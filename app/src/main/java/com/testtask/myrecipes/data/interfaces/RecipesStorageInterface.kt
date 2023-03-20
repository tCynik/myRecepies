package com.testtask.myrecipes.data.interfaces

import com.testtask.myrecipes.domain.models.SingleRecipe
import java.util.*

interface RecipesStorageInterface {
    fun loadRecipesData(): SortedMap<String, SingleRecipe>?

    fun saveRecipesData(recipe: SortedMap<String, SingleRecipe>)
}