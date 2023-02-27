package com.testtask.myrecipes.data.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe

/**
 * класс для работы с внутренней бД
 * Сохранение-загрузка таблицы рецептов происходит с помощью SQLite
 */

class Storage(val dataBaseHelper: HelperInterface) : RecipesStorageInterface {
    val tableName = TableConstance.TABLE_RECIPES.value()
    var database: SQLiteDatabase? = null
    init {
        database = dataBaseHelper.getWritableDatabase()
    }

    override fun loadRecipesData(): List<SingleRecipe> {
        // получаем набор строк с данными: Cursor
        val cursor = database!!.query(tableName, null, null, null, null, null, null)
        val resultData = mutableListOf<SingleRecipe>()

        // перебираем курсор построчно
        if (cursor.moveToFirst()) { // активизируем первую запись курсора, если она вообще есть
            var isHasNext = true
            while (isHasNext) {
                resultData.add(parseNextCursorLine(cursor))
                if (cursor.moveToNext()) isHasNext = false
            }
            val index = cursor.getColumnIndex(TableConstance.KEY_ID.value())

        } else {} // todo: обработка того, что БД пустая

        val db = dataBaseHelper.getReadableDatabase()
        return resultData
    }

    private fun parseNextCursorLine(cursor: Cursor): SingleRecipe {
        val full_image = PictureModel(
            networkAddress = TableConstance.KEY_IMAGE_LINK_FULL.value(),
            localAddress = TableConstance.KEY_IMAGE_STORAGE_FULL.value() ,
            null) // todo: организовать подгрузку картинки
        val pre_image = PictureModel(
            networkAddress = TableConstance.KEY_IMAGE_LINK_PRE.value(),
            localAddress = TableConstance.KEY_IMAGE_STORAGE_PRE.value() ,
            null) // todo: организовать подгрузку картинки
        return SingleRecipe(
            id = cursor.getColumnIndex(TableConstance.KEY_ITEM.value()).toString(),
            name = cursor.getColumnIndex(TableConstance.KEY_NAME.value()).toString(),
            headline = cursor.getColumnIndex(TableConstance.KEY_HEADLINE.value()).toString(),
            description = cursor.getColumnIndex(TableConstance.KEY_DESCRIPTION.value()).toString(),
            difficulty = cursor.getColumnIndex(TableConstance.KEY_DIFFICULTY.value()),
            calories = cursor.getColumnIndex(TableConstance.KEY_CALORIES.value()).toString(),
            fats = cursor.getColumnIndex(TableConstance.KEY_FATS.value()).toString(),
            proteins = cursor.getColumnIndex(TableConstance.KEY_PROTEINS.value()).toString(),
            carbos = cursor.getColumnIndex(TableConstance.KEY_CARBOS.value()).toString(),
            cookingTime = cursor.getColumnIndex(TableConstance.KEY_TIME.value()).toString(),
            full_image = full_image,
            pre_image = pre_image)
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

        database!!.insert(tableName, null, contentValues)
    }
}