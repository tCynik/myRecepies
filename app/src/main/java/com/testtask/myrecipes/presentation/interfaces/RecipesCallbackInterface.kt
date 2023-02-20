package com.testtask.myrecipes.presentation.interfaces

import com.testtask.myrecipes.domain.models.SingleRecipe

interface RecipesCallbackInterface {
    fun onGotRecipesData(data: List<SingleRecipe>)
}