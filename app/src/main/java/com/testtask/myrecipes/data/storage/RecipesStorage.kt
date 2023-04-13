package com.testtask.myrecipes.data.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger
import java.util.*

/**
 * класс для работы с внутренней бД
 * Сохранение-загрузка таблицы рецептов происходит с помощью SQLite
 * база работает ТОЛЬКО с памятью рецептов, самы файлы фоток сохраняются отдельно, в рецепте только адреса. Фотки подключаются пакетом image_load_save
 */

class RecipesStorage(
    val context: Context,
    dataBaseHelper: HelperInterface,
    val logger: ToasterAndLogger
) : RecipesStorageInterface {
    val tableName = TableConstance.TABLE_RECIPES.value()
    var database: SQLiteDatabase? = null

    init {
        database = dataBaseHelper.getWritableDatabase()
    }

    override fun loadRecipesData(): SortedMap<String, SingleRecipe>? {
        Log.i ("bugfix: recipesStorage", "loading recipes data")
        // получаем набор строк с данными: Cursor
        val cursor = database!!.query(tableName, null, null, null, null, null, null)
        val resultData: SortedMap<String, SingleRecipe> = sortedMapOf()

        // перебираем курсор построчно
        val parser = CursorParser(logger = logger)
        if (cursor.moveToFirst()) { // активизируем первую запись курсора, если она вообще есть
            var isHasNext = true
            while (isHasNext) {
                val recipe = parser.parse(cursor)
                resultData[recipe.id] = recipe//.add(parser.parse(cursor))
                isHasNext = cursor.moveToNext()
            }
        } else { // todo: обработка того, что БД пустая
            return null
        }

        return resultData
    }

    override fun saveRecipesData(recipes: SortedMap<String, SingleRecipe>) {
        val iterator = recipes.iterator()
        while (iterator.hasNext()) {
            val recipe: SingleRecipe = iterator.next().value
            saveSingleRecipe(recipe)
        }
    }

    override fun saveSingleRecipe(recipe: SingleRecipe) {
        Log.i("bugfix: recipesStorage", "saving recipe with ID = ${recipe.id}, has fullPicture address = ${recipe.full_image.localAddress}") // make toast

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
        contentValues.put(
            TableConstance.KEY_IMAGE_LINK_FULL.value(),
            recipe.full_image.networkAddress
        )
        contentValues.put(
            TableConstance.KEY_IMAGE_STORAGE_FULL.value(),
            recipe.full_image.localAddress
        )
        contentValues.put(
            TableConstance.KEY_IMAGE_LINK_PRE.value(),
            recipe.pre_image.networkAddress
        )
        contentValues.put(
            TableConstance.KEY_IMAGE_STORAGE_PRE.value(),
            recipe.pre_image.localAddress
        )

        database!!.insert(tableName, null, contentValues)
    }
}