package com.testtask.myrecipes.data.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// константы БД
const val DATABASE_VERSION = 1
const val DATABASE_NAME = "Recipes_database"
const val TABLE_RECIPES = "Recipes_list"

// константы заголовков таблицы
const val KEY_ID = "_id"
const val KEY_ITEM = "itemId"
const val KEY_NAME = "name"
const val KEY_DESCRIPTION = "description"
const val KEY_HEADLINE = "headline"
const val KEY_DIFFICULTY = "difficulty"
const val KEY_CALORIES = "calories"
const val KEY_FATS = "fats"
const val KEY_PROTEINS = "proteins"
const val KEY_CARBOS = "carbos"
const val KEY_TIME = "time"
const val KEY_IMAGE_LINK_FULL = "image"
const val KEY_IMAGE_STORAGE_FULL = "image"
const val KEY_IMAGE_LINK_PRE = "preimage"
const val KEY_IMAGE_STORAGE_PRE = "preimage"

class DataBaseHelper( // курсор передаем налловый, название и версию берем из констант
    context: Context?,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), HelperInterface {

    override fun onCreate(p0: SQLiteDatabase?) { // если таблицы пока нет, создаем и размечаем ее зановоо
        val tableString: String = ("create table" + TABLE_RECIPES + "(" + KEY_ID + " integer primary key" +
                "," + KEY_ITEM + " text" +
                "," + KEY_NAME + " name" +
                "," + KEY_DESCRIPTION + " text" +
                "," + KEY_HEADLINE + " text" +
                "," + KEY_DIFFICULTY + " integer" +
                "," + KEY_CALORIES + " text" +
                "," + KEY_PROTEINS + " text" +
                "," + KEY_CARBOS + " text" +
                "," + KEY_TIME + " text" +
                "," + KEY_IMAGE_LINK_FULL + " text" + // net link for download the image
                "," + KEY_IMAGE_STORAGE_FULL + " text" + // local storage address of the image file
                "," + KEY_IMAGE_LINK_PRE + " text" +
                "," + KEY_IMAGE_STORAGE_PRE + " text" + ")")
        p0?.execSQL(tableString)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}

