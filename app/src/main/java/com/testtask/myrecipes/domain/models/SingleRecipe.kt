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
    val calories: Int,
    val fats: Int,
    val proteins: Int,
    val carbos: Int,
    val cookingTine: Int,
    val full_image: PictureModel, // в модели класса Data тут будет имя файла
    val pre_image: PictureModel, // в модели класса Data тут будет имя файла
    ) {

    // todo: descriprion is long text, maby an other type?
}