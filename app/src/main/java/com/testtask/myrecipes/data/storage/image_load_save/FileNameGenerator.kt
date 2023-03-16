package com.testtask.myrecipes.data.storage.image_load_save

class FileNameGenerator {
    fun getName(fileName: String, isFull:Boolean): String {
        return if (isFull) fileName + "_full"
        else fileName + "_pre"
    }
}