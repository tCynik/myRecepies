package com.testtask.myrecipes.presentation.interfaces

import com.testtask.myrecipes.domain.models.SingleRecipe
import java.util.*

interface RecipesCallbackInterface {
    fun onGotRecipesData(data: SortedMap<String, SingleRecipe>)
}