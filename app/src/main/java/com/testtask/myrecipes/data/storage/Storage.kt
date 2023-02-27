package com.testtask.myrecipes.data.storage

import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe

/**
 * класс для работа с внутренней бД
 * Сохранение-загрузка таблицы рецептов происходит с помощью SQLite
 */

class Storage(dataBaseHelper: HelperInterface) : RecipesStorageInterface {
    override fun loadRecipesData(): List<SingleRecipe> {
        TODO("Not yet implemented")
    }

    override fun saveRecipesData(recipes: List<SingleRecipe>) {
        recipes.forEach{recipe -> saveSingleRecipe(recipe)}
    }

    private fun saveSingleRecipe(recipe: SingleRecipe) {

    }
}

