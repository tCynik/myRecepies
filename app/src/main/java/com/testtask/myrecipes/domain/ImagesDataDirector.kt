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
 * класс отвечает за управление загрузкой-сохранением фоток.
 * Для каждого рецепта загрузка преью, либо целой фотки, происходит одинаково, только для разрых имен (_pre либо _full)
 * действия производятся асинхронно, поэтому вывод результата через коллбек по готовности
 * После того, как фотография газрузилась, она кешируется, при этом в инстансе рецепта делается соответствующая запись.
 *
 * принцип работы класса: на вход получаем рецепт, проверяем есть ли на нем фотка. Если фотка есть, берем ее
 * из памяти, вставляем инстанс рецепта, и возвращаем измененный инстанс. Если фотки нет, либо загрузка не удалась
 * - асинхронно качаем ее из сети, сохраняем в сторейдж, и так же кладем в инстанс рецепта.
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
        // формируем транскрипцию локальных и удаленных адресов для дальнейшей работы
        var localAddress = ""
        var networkAddress = ""
        if (isFull) {
            localAddress = recipe.full_image.localAddress
            networkAddress = recipe.full_image.networkAddress
        } else {
            localAddress = recipe.pre_image.localAddress
            networkAddress = recipe.pre_image.networkAddress
        }
        Log.i ("bugfix: DataDirector", "getting image for recipe ${recipe.id}, is full = $isFull, network address = $networkAddress")

        var picture: Drawable?
        var fileName = recipe.id // будущее имя файла
        var resultRecipe: SingleRecipe?

        picture = imageLoader.loadImageByFileAddress(localAddress)
        if (picture != null) {
            Log.i("bugfix: ImagesDataDirector", "image ${recipe.id} was loaded from local, updating")
            resultRecipe = updateRecipe(
                recipe = recipe,
                picture = picture,
                localAddress = localAddress,
                isFull = isFull)

            imageCallback.updateRecipeItemNoSave(resultRecipe!!) // пошел коллбек с результатом

        } else {
            Log.i("bugfix: ImagesDataDirector", "loading image ${recipe.id} from local is failed, starting load one from net")

            fileName = FileNameGenerator().getName(fileName, isFull)

            picture = imageDownloader.downloadPicture(networkAddress, fileName) // качаем фото из сети
            Log.i(
                "bugfix: DataDirector",
                "the picture download ended. Picture downloaded - ${picture != null}, is full = $isFull "
            )

            if (picture != null) {
                val localAddress = imageSager.saveImage(image = picture, fileName = fileName)
                Log.i(
                    "bugfix: ImagesDataDirector",
                    "image ${recipe.id} full = $isFull was downloaded, saved to $localAddress"
                )
                if (localAddress != NO_LOCAL_IMAGE_PATTERN) {// если сохранение успешно, то с учетом записи адреса файла
                    resultRecipe = updateRecipe( // обновляем рецепт
                        recipe = recipe,
                        picture = picture,
                        localAddress = localAddress,
                        isFull = isFull
                    )
                    // сохраняем измененный рецепт, обновляем данные о нем в UI
                    imageCallback.updateRecipeItemAndSave(resultRecipe!!)
                }
            } else {
                logger.printToast("image cannot be loaded")
                logger.printLog("image ${recipe.id} was not loaded from local and remote recourse both")
            }
        }
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