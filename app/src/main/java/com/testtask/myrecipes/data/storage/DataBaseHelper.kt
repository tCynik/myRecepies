package com.testtask.myrecipes.data.storage

/**
 * хелпер для работы с базой данных
 */
//todo: константы названий толбцов хранятся в этом месте. В то же время, эти данные нужны в Storage
// для управления парсингом данных из БД. напрямю эти константы передать невозможно, для создания
// геттеров потребуетя прописывать эти же геттеры в интерфейсе хелпера, что сильно усложнит
// тестирование.
// upd: Пока вынес в enum, подумать, насколько верное решение


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// константы БД
const val DATABASE_VERSION = 1
const val DATABASE_NAME = "Recipes_database"
const val TABLE_RECIPES = "Recipes_list"

class DataBaseHelper( // курсор передаем налловый, название и версию берем из констант
    context: Context?,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), HelperInterface {

    override fun onCreate(p0: SQLiteDatabase?) { // если таблицы пока нет, создаем и размечаем ее зановоо
        val tableString: String = ("create table $TABLE_RECIPES " +
                "( ${TableConstance.KEY_ID.value()} integer primary key" +
                ", ${TableConstance.KEY_ITEM.value()} text" +
                ", ${TableConstance.KEY_NAME.value()} text" +
                ", ${TableConstance.KEY_DESCRIPTION.value()} text" +
                ", ${TableConstance.KEY_HEADLINE.value()} text" +
                ", ${TableConstance.KEY_DIFFICULTY.value()} integer" +
                ", ${TableConstance.KEY_CALORIES.value()} text" +
                ", ${TableConstance.KEY_FATS.value()} text" +
                ", ${TableConstance.KEY_PROTEINS.value()} text" +
                ", ${TableConstance.KEY_CARBOS.value()} text" +
                ", ${TableConstance.KEY_TIME.value()} text" +
                ", ${TableConstance.KEY_IMAGE_LINK_FULL.value()} text" +
                ", ${TableConstance.KEY_IMAGE_STORAGE_FULL.value()} text" +
                ", ${TableConstance.KEY_IMAGE_LINK_PRE.value()} text" +
                ", ${TableConstance.KEY_IMAGE_STORAGE_PRE.value()} text)")
        p0?.execSQL(tableString)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropCommand = ("drop table if exist + $TABLE_RECIPES")
        p0?.execSQL(dropCommand)

        onCreate(p0)
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        return super.getReadableDatabase()
    }
}

