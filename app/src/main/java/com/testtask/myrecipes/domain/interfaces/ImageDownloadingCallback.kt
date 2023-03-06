package com.testtask.myrecipes.domain.interfaces

import com.testtask.myrecipes.domain.models.SingleRecipe

interface ImageDownloadingCallback {
    fun updateRecipeItem(recipe: SingleRecipe)
}