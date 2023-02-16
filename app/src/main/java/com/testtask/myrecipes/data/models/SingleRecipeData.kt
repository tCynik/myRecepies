package com.testtask.myrecipes.data.models

import android.media.Image

data class SingleRecipeData(
    val id: Long,
    val name: String,
    val description: String,
    val headline: String,
    val difficulty: Int,
    val calories: Int,
    val fats: Int,
    val proteins: Int,
    val carbos: Int,
    val cookingTine: Int,
    val full_image_address: String, // в модели класса Data тут будет имя файла
    val pre_image_address: String, // в модели класса Data тут будет имя файла
) {
}