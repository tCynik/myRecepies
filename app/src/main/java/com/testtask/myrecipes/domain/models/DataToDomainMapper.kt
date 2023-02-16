package com.testtask.myrecipes.domain.models

import com.testtask.myrecipes.data.models.SingleRecipeData

/**
 * класс для превращения модели SingleRecipe как результат парсинга - из слоя Data в слой Domain
 */
class DataToDomainMapper {
    fun execute(dataModel: SingleRecipeData): SingleRecipe {
        return SingleRecipe(
            id = dataModel.id,
            name = dataModel.name,
            description = dataModel.description,
            headline = dataModel.headline,
            difficulty = dataModel.difficulty,
            calories = dataModel.calories,
            fats = dataModel.fats,
            proteins = dataModel.proteins,
            carbos = dataModel.carbos,
            cookingTine = dataModel.cookingTine,
            full_image = PictureModel(
                networkAddress = dataModel.full_image_address,
                localAddress = "",
                image = null),
            pre_image = PictureModel(
                networkAddress = dataModel.pre_image_address,
                localAddress = "",
                image = null)
        )
    }
}