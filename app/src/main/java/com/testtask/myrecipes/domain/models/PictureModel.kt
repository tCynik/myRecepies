package com.testtask.myrecipes.domain.models

import android.media.Image

/**
 * Модель, хранящая картинку. Если картинка Null (еще не подгружена), можно:
 * воспользоваться адресом для скачки из сети, либо именем для загрузки с локального хранилища
 */

data class PictureModel(val networkAddress: String,
                        val localAddress: String,
                        val image: Image?)
