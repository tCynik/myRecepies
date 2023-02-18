package com.testtask.myrecipes.data.models

data class SingleRecipeData(
    val id: String,
    val name: String,
    val description: String,
    val headline: String,
    val difficulty: Int,
    val calories: String,
    val fats: String,
    val proteins: String,
    val carbos: String,
    val cookingTime: String,
    val full_image_address: String, // в модели класса Data тут будет имя файла
    val pre_image_address: String, // в модели класса Data тут будет имя файла
) {
}