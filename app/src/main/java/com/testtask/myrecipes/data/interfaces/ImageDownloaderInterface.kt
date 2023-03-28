package com.testtask.myrecipes.data.interfaces

import android.graphics.drawable.Drawable

interface ImageDownloaderInterface {
    fun downloadPicture (addressURL: String, fileName: String): Drawable?

}