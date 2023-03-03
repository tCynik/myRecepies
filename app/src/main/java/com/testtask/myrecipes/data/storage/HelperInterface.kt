package com.testtask.myrecipes.data.storage

import android.database.sqlite.SQLiteDatabase

/**
 * интерфейс для передачи Storage при его тестировании фейкового хелпера
 */
interface HelperInterface {
    fun getWritableDatabase(): SQLiteDatabase?

    fun getReadableDatabase(): SQLiteDatabase?
}
