package com.testtask.myrecipes.data.storage

import android.content.Context
import android.database.Cursor
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger

/**
 * класс для парсинга результата SQL запроса
 */
// todo: сейчас сразу же происходит загрузка фото из сторейджа. Рекомендуется оставить без фотки и организовать загрузку отдельно.

class CursorParser(val context: Context, logger: ToasterAndLogger) {
    val imageLoader = ImageLoader(context, logger)
    fun parse(cursor: Cursor): SingleRecipe {
        val idIndex = cursor.getColumnIndex(TableConstance.KEY_ITEM.value())
        val nameIndex = cursor.getColumnIndex(TableConstance.KEY_NAME.value())
        val headlineIndex = cursor.getColumnIndex(TableConstance.KEY_HEADLINE.value())
        val descriptionIndex = cursor.getColumnIndex(TableConstance.KEY_DESCRIPTION.value())
        val difficultyIndex = cursor.getColumnIndex(TableConstance.KEY_DIFFICULTY.value())
        val caloriesIndex = cursor.getColumnIndex(TableConstance.KEY_CALORIES.value())
        val fatsIndex = cursor.getColumnIndex(TableConstance.KEY_FATS.value())
        val proteinsIndex = cursor.getColumnIndex(TableConstance.KEY_PROTEINS.value())
        val carbosIndex = cursor.getColumnIndex(TableConstance.KEY_CARBOS.value())
        val cookingTimeIndex = cursor.getColumnIndex(TableConstance.KEY_TIME.value())
        val full_imageLinkIndex = cursor.getColumnIndex(TableConstance.KEY_IMAGE_LINK_FULL.value())
        val full_imageAddressIndex = cursor.getColumnIndex(TableConstance.KEY_IMAGE_STORAGE_FULL.value())
        val pre_imageLinkIndex = cursor.getColumnIndex(TableConstance.KEY_IMAGE_LINK_PRE.value())
        val pre_imageAddressIndex = cursor.getColumnIndex(TableConstance.KEY_IMAGE_STORAGE_PRE.value())

        val fullImageAddress = cursor.getString(full_imageAddressIndex)
        val preImageAddress = cursor.getString(pre_imageAddressIndex)

        return SingleRecipe(
            id = cursor.getString(idIndex),
            name = cursor.getString(nameIndex),
            headline = cursor.getString(headlineIndex),
            description = cursor.getString(descriptionIndex),
            difficulty = cursor.getInt(difficultyIndex),
            calories = cursor.getString(caloriesIndex),
            fats = cursor.getString(fatsIndex),
            proteins = cursor.getString(proteinsIndex),
            carbos = cursor.getString(carbosIndex),
            cookingTime = cursor.getString(cookingTimeIndex),
            full_image = PictureModel(
                networkAddress = cursor.getString(full_imageLinkIndex),
                localAddress = fullImageAddress,
                image = imageLoader.loadImageByFileName(fullImageAddress)),
            pre_image = PictureModel(
                networkAddress = cursor.getString(pre_imageLinkIndex),
                localAddress = preImageAddress,
                image = imageLoader.loadImageByFileName(preImageAddress))
        )
    }
}