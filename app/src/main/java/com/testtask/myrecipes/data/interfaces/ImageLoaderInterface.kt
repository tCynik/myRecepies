package com.testtask.myrecipes.data.interfaces

import android.graphics.drawable.Drawable

interface ImageLoaderInterface {
    fun loadImageByFileAddress(localAddress: String): Drawable?
}