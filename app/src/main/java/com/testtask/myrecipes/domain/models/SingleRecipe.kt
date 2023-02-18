package com.testtask.myrecipes.domain.models

import android.media.Image

/**
 * Модель отвечающая за хранение данных каждого отдельного рецепта
 */
data class SingleRecipe(
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
    val full_image: PictureModel, // в модели класса Data тут будет имя файла
    val pre_image: PictureModel, // в модели класса Data тут будет имя файла
    ) {

    // todo: descriprion is long text, maby an other type?
}