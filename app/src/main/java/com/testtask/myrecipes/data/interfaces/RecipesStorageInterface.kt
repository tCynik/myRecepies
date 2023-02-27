package com.testtask.myrecipes.data.interfaces

import com.testtask.myrecipes.domain.models.SingleRecipe

interface RecipesStorageInterface {
    fun loadRecipesData(): List<SingleRecipe>

    fun saveRecipesData(recipe: List<SingleRecipe>)
}