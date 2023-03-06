package com.testtask.myrecipes.domain.models

import android.graphics.drawable.Drawable

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

    fun setFullImage(image: Drawable, localAddress: String): SingleRecipe {
        val full_image = PictureModel(networkAddress = this.full_image.networkAddress,
            localAddress = localAddress, // todo: need2 change local address field!
            image = image)
        return updateRecipe(full_imageNew = full_image, pre_imageNew = this.pre_image)
    }

    fun setPreImage(image: Drawable, localAddress: String): SingleRecipe {
        val pre_image = PictureModel(networkAddress = this.pre_image.networkAddress,
            localAddress = localAddress, // todo: need2 change local address field!
            image = image)
        return updateRecipe(full_imageNew = this.full_image, pre_imageNew = pre_image)
    }

    private fun updateRecipe(full_imageNew: PictureModel, pre_imageNew: PictureModel): SingleRecipe {
        return SingleRecipe(
            id = this.id,
            name = this.name,
            description = this.description,
            headline = this.headline,
            difficulty = this.difficulty,
            calories = this.calories,
            fats = this.fats,
            proteins = this.proteins,
            carbos = this.carbos,
            cookingTime = this.cookingTime,
            full_image = full_imageNew,
            pre_image = pre_imageNew)
    }

    // todo: descriprion is long text, maby an other type?
}