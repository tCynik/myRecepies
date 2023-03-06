package com.testtask.myrecipes.domain

import android.graphics.drawable.Drawable
import com.testtask.myrecipes.data.network.models.ImageDownloader
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.interfaces.ImageDownloadingCallback
import com.testtask.myrecipes.domain.models.SingleRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * класс отвечает за управление асинхронной загрузкой-сохранением фоток,
 * а так же за проверку наличия фоток в памяти???
 * В зависимости от выбранного варианта работаем для полной картинки или предварительной
 * действия производятся асинхронно, поэтому вывод результата через коллбек по готовности
 *
 * принцип работы: берем конкретный рецепт, проверяем есть ли фотки. Если фотка есть, берем ее
 * из памяти, вставляем в ячейку даты, обновляем дату (пока что всю, только потом
 * построчно сделаем). Если фотки нет - асинхронно качаем ее, сохраняем в сторейдж,
 * и опять обновляем дату во ВМ.
 */

private const val NO_LOCAL_IMAGE_PATTERN = "EMPTY"

class ImagesDataDirector(
    val imageCallback: ImageDownloadingCallback,
    val scope: CoroutineScope,
    val imageLoader: ImageLoader,
    val imageSager: ImageSaver,
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

        // асинхронно качаем фотку
        scope.launch(Dispatchers.Default) {
            var picture: Drawable? = null
            var resultRecipe: SingleRecipe? = null
            if (localAddress == NO_LOCAL_IMAGE_PATTERN) { // если локальной фотки нет, качаем ее из сети
                picture = imageDownloader.downloadPicture(networkAddress) // качаем фото из сети
                if (picture != null) {
                    // сохраняем фото в память
                    var fileName = recipe.id // формируем будущий адрес сохранения
                    fileName = if (isFull) fileName + "_full"
                    else fileName + "_pre"

                    // сохраняем
                    val localAddress = imageSager.saveImage(image = picture, fileName = fileName)
                    if (localAddress != NO_LOCAL_IMAGE_PATTERN)
                        resultRecipe = picture?.let { updateRecipe(
                            recipe = recipe,
                            picture = it,
                            localAddress = localAddress,
                            isFull = isFull) }
                }
            } else { // если локальная есть, берем из памяти
                picture = imageLoader.loadImageByAddress(localAddress)
                val localAddress = if (isFull) recipe.full_image.localAddress
                else recipe.pre_image.localAddress
                if (isFull)
                resultRecipe = picture?.let { updateRecipe(
                    recipe = recipe,
                    picture = it,
                    localAddress = localAddress,
                    isFull = isFull) }
            }
            imageCallback.updateRecipeItem(resultRecipe!!)
        }
    }

    private fun updateRecipe (
        recipe: SingleRecipe,
        picture: Drawable,
        localAddress: String,
        isFull: Boolean): SingleRecipe {
        return if (isFull) {
            recipe.setFullImage(image = picture, localAddress = localAddress)
        } else {
            recipe.setPreImage(image = picture, localAddress = localAddress)
        }
    }
}