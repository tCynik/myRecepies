package com.testtask.myrecipes.data.interfaces

import android.graphics.drawable.Drawable

interface ImageSaverInterface {
    fun saveImage(image: Drawable, fileName: String): String
}