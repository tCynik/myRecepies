package com.testtask.myrecipes.domain.interfaces

import com.testtask.myrecipes.domain.models.SingleRecipe

interface ResponseResultBacallInterface {
    fun onGetResult(result: List<SingleRecipe>)
}