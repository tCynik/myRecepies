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
 * база работает ТОЛЬКО с памятью рецептов, не включая фотки. Фотки подключаются пакетом image_load_save
 */

class RecipesStorage(
    val context: Context,
    val dataBaseHelper: HelperInterface,
    val logger: ToasterAndLogger
) : RecipesStorageInterface {
    val tableName = TableConstance.TABLE_RECIPES.value()
    var database: SQLiteDatabase? = null

    init {
        database = dataBaseHelper.getWritableDatabase()
    }

    override fun loadRecipesData(): SortedMap<String, SingleRecipe>? {
        // получаем набор строк с данными: Cursor
        val cursor = database!!.query(tableName, null, null, null, null, null, null)
        val resultData: SortedMap<String, SingleRecipe> = sortedMapOf()

        // перебираем курсор построчно
        val parser = CursorParser(context = context, logger = logger)
        if (cursor.moveToFirst()) { // активизируем первую запись курсора, если она вообще есть
            var isHasNext = true
            while (isHasNext) {
                val recipe = parser.parse(cursor)
                resultData[recipe.id] = recipe//.add(parser.parse(cursor))
                if (cursor.moveToNext()) isHasNext = false
            }
            val index = cursor.getColumnIndex(TableConstance.KEY_ID.value())
            Log.i("bugfix: recipesStorage", "loading recipes data. loaded  = ${resultData.size}") // make toast
        } else { // todo: обработка того, что БД пустая
            return null
        }

        val db = dataBaseHelper.getReadableDatabase()
        return resultData
    }

    override fun saveRecipesData(recipes: SortedMap<String, SingleRecipe>) {
        val iterator = recipes.iterator()
        while (iterator.hasNext()) {
            val recipe: SingleRecipe = iterator.next().value
            Log.i("bugfix: recipesStorage", "saving recipe with ID = ${recipe.id}") // make toast

            saveSingleRecipe(recipe)
        }
        //recipes.forEach{recipe -> saveSingleRecipe(recipe)}
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
            recipe.pre_image.networkAddress
        )

        database!!.insert(tableName, null, contentValues)
    }
}