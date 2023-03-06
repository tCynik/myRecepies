package com.testtask.myrecipes.domain

import android.graphics.drawable.Drawable
import com.testtask.myrecipes.data.network.models.ImageDownloader
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * класс отвечает за управление асинхронной загрузкой-сохранением фоток,
 * а так же за проверку наличия фоток в памяти???
 */

private const val noLocalImagePattern = "EMPTY"
class ImagesDataDirector(
    val imageCallback: ImageDownloadingCallback,
    val scope: CoroutineScope,
    val imageLoader: ImageLoader,
    val imageDownloader: ImageDownloader) { // todo: Injection: interface with callback of recipe with photos; storages

    fun getImage(recipe: SingleRecipe, isFull: Boolean) {
        var localAddress = ""
        var networkAddress = ""
        if (isFull) {
            localAddress = recipe.full_image.localAddress
            networkAddress = recipe.full_image.networkAddress
        } else {
            localAddress = recipe.pre_image.localAddress
            networkAddress = recipe.pre_image.networkAddress
        }

        scope.launch(Dispatchers.Default) {
            var picture: Drawable? = null
            var resultRecipe: SingleRecipe? = null
            if (localAddress == noLocalImagePattern) {
                // качаем фотку из сети
                picture = imageDownloader.downloadPicture(networkAddress)
                resultRecipe = picture?.let { updateRecipe(recipe = recipe, picture = it, isFull = isFull) }
            } else {
                picture = imageLoader.loadImageByAddress(localAddress)
                resultRecipe = picture?.let { updateRecipe(recipe = recipe, picture = it, isFull = isFull) }
            }
            imageCallback.updateRecipeItem(resultRecipe!!)
        }
    }

    private fun updateRecipe (recipe: SingleRecipe, picture: Drawable, isFull: Boolean): SingleRecipe {
        if (isFull) {
            return recipe.setFullImage(picture)
        } else {
            return recipe.setPreImage(picture)
        }
    }
}