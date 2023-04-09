package com.testtask.myrecipes.domain.models

import android.graphics.drawable.Drawable

/**
 * Модель отвечающая за хранение данных каждого отдельного рецепта
 */
data class SingleRecipe(
    val id: String, // этот айдишник берётся в качестве имя файла для сохранения картинок, +_full / +_pre в зависимости от типа картинки
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

    override fun equals(other: Any?): Boolean {
        var isEquals = true
        if (other is SingleRecipe) {
            if (other.id != this.id) isEquals = false
            if (other.name != this.name) isEquals = false
            if (other.description != this.description) isEquals = false
            if (other.headline != this.headline) isEquals = false
            if (other.difficulty != this.difficulty) isEquals = false
            if (other.calories != this.calories) isEquals = false
            if (other.fats != this.fats) isEquals = false
            if (other.proteins != this.proteins) isEquals = false
            if (other.carbos != this.carbos) isEquals = false
            if (other.cookingTime != this.cookingTime) isEquals = false
            if (other.full_image.networkAddress != this.full_image.networkAddress) isEquals = false
            if (other.pre_image.networkAddress != this.pre_image.networkAddress) isEquals = false
            return isEquals
        } else return false
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    // todo: descriprion is long text, maby an other type?
}