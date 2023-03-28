package com.testtask.myrecipes.domain

import android.graphics.drawable.Drawable
import android.util.Log
import com.testtask.myrecipes.data.interfaces.ImageDownloaderInterface
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.data.interfaces.ImageSaverInterface
import com.testtask.myrecipes.data.storage.image_load_save.FileNameGenerator
import com.testtask.myrecipes.domain.interfaces.ImageDownloadingCallback
import com.testtask.myrecipes.domain.models.SingleRecipe
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger


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
    val imageLoader: ImageLoaderInterface,
    val imageSager: ImageSaverInterface,//todo: Injection: interface with callback of recipe with photos; storages
    val imageDownloader: ImageDownloaderInterface,
    val logger: ToasterAndLogger
) { //

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

        var picture: Drawable?
        var fileName = recipe.id // будущее имя файла
        var resultRecipe: SingleRecipe? = null

        /**
         * логика загрузки такая: пытаемся тянуть фото из сети. (это новое фото)
         * Стянутые из сети фото пересохраняем на старое место. (обновляем фото)
         * Если к сети подрубиться не можем, тянем фото из памяти (это старое фото)
         */

        fileName = FileNameGenerator().getName(fileName, isFull)

        picture = imageDownloader.downloadPicture(networkAddress, fileName) // качаем фото из сети

        if (picture != null) { // если скачали из сети успешно, сохраняем фото
            val localAddress = imageSager.saveImage(image = picture, fileName = fileName)
            Log.i("bugfix: ImagesDataDirector", "image ${recipe.id} was downloaded, saved to $localAddress")
            if (localAddress != NO_LOCAL_IMAGE_PATTERN) {// если сохранение успешно, то с учетом записи адреса файла
                resultRecipe = picture?.let {
                    updateRecipe( // обновляем рецепт
                        recipe = recipe,
                        picture = it,
                        localAddress = localAddress,
                        isFull = isFull
                    )
                }

                imageCallback.updateRecipeItemAndSave(resultRecipe!!)
            }

        } else { // если скачивание из сети неуспешно, идем в память
            picture = imageLoader.loadImageByFileAddress(localAddress)
            if (picture != null) {
                Log.i("bugfix: ImagesDataDirector", "image ${recipe.id} was not downloaded, loading one from local")
                resultRecipe = updateRecipe(
                    recipe = recipe,
                    picture = picture,
                    localAddress = localAddress,
                    isFull = isFull)
            }

            else {
                logger.printToast("image cannot be loaded")
                logger.printLog("image ${recipe.id} was not loaded from local and remote recourse both")
            }
        }

        if (picture != null) // если картинка всё же есть, обновляем рецепт
            imageCallback.updateRecipeItemNoSave(resultRecipe!!) // пошел коллбек с результатом

    }

    private fun updateRecipe(
        recipe: SingleRecipe,
        picture: Drawable,
        localAddress: String,
        isFull: Boolean
    ): SingleRecipe {
        return if (isFull) {
            recipe.setFullImage(image = picture, localAddress = localAddress)
        } else {
            recipe.setPreImage(image = picture, localAddress = localAddress)
        }
    }

}