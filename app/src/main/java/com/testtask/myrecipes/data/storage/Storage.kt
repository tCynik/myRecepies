package com.testtask.myrecipes.data.storage

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe

/**
 * класс для работа с внутренней бД
 * Сохранение-загрузка таблицы рецептов происходит с помощью SQLite
 */

class Storage(val dataBaseHelper: HelperInterface) : RecipesStorageInterface {
    var database: SQLiteDatabase? = null
    init {
        database = dataBaseHelper.getWritableDatabase()
    }

    override fun loadRecipesData(): List<SingleRecipe> {
        val db = dataBaseHelper.getReadableDatabase()
        TODO("Not yet implemented")
    }

    override fun saveRecipesData(recipes: List<SingleRecipe>) {
        recipes.forEach{recipe -> saveSingleRecipe(recipe)}
    }

    private fun saveSingleRecipe(recipe: SingleRecipe) {
        val contentValues = ContentValues()
        contentValues.put(TableConstance.KEY_ITEM.value(), recipe.id)
        contentValues.put(TableConstance.KEY_NAME.value(), recipe.name)
        contentValues.put(TableConstance.KEY_DESCRIPTION.value(), recipe.description)
        contentValues.put(TableConstance.KEY_HEADLINE.value(), recipe.headline)
        contentValues.put(TableConstance.KEY_DIFFICULTY.value(), recipe.difficulty)
        contentValues.put(TableConstance.KEY_CALORIES.value(), recipe.calories)
        contentValues.put(TableConstance.KEY_FATS.value(), recipe.fats)
        contentValues.put(TableConstance.KEY_PROTEINS.value(), recipe.proteins)
        contentValues.put(TableConstance.KEY_CARBOS.value(), recipe.carbos)
        contentValues.put(TableConstance.KEY_TIME.value(), recipe.cookingTime)
        contentValues.put(TableConstance.KEY_IMAGE_LINK_FULL.value(), recipe.full_image.networkAddress)
        contentValues.put(TableConstance.KEY_IMAGE_STORAGE_FULL.value(), recipe.full_image.localAddress)
        contentValues.put(TableConstance.KEY_IMAGE_LINK_PRE.value(), recipe.pre_image.networkAddress)
        contentValues.put(TableConstance.KEY_IMAGE_STORAGE_PRE.value(), recipe.pre_image.networkAddress)

        //database??.insert()
    }
}