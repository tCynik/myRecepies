package com.testtask.myrecipes.domain

import android.graphics.drawable.Drawable
import android.util.Log
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.storage.image_load_save.FileNameGenerator
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.interfaces.ImageDownloadingCallback
import com.testtask.myrecipes.domain.models.SingleRecipe


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
    val imageLoader: ImageLoader,
    val imageSager: ImageSaver,
    val imageDownloader: ImageDownloader
) { // todo: Injection: interface with callback of recipe with photos; storages

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
         * ну конечно же не будет адреса сохранения, т.к. мы список только что скачали из сети, и
         * в девичестве полоили тут.
         * Нужно производить поиск непосредственно на устройстве
         */

        //todo: 1. логика загрузки такая: пытаемся тянуть фото из сети. Стянутые фото пересохраняем на старое место.
        // Если к сети подрубиться не можем, тянем фото из памяти

        if (localAddress == NO_LOCAL_IMAGE_PATTERN) { // если в рецепте записи о локальной фотке нет, ищем на диске либо качаем ее из сети
            fileName = FileNameGenerator().getName(fileName, isFull)
            Log.i("bugfix: ImagesDirector", "loading file name = $fileName")
            picture = imageLoader.loadImageByFileName(fileName)

            if (picture == null) { // на диске тоже нет - значит, качаем из сети
                Log.i("bugfix: imagesDataDirector", "no saved picture, downloading from net")

                picture = imageDownloader.downloadPicture(networkAddress, fileName) // качаем фото из сети

                if (picture != null) { // если скачали из сети успешно,
                    // сохраняем фото в память
                    fileName = FileNameGenerator().getName(fileName, isFull)
                    Log.i("bugfix: imagesDataDirector", "second name generation = $fileName")

                    // todo picture saving must be in manager??
                    // сохраняем в памяти
                    val localAddress = imageSager.saveImage(image = picture, fileName = fileName)
                    Log.i("bugfix: ImagesDataDirector", "image was saved to $localAddress")
                    if (localAddress != NO_LOCAL_IMAGE_PATTERN) // если сохранение успешно
                    // обновляем инстанс модели с учетом файла фото
                        resultRecipe = picture?.let {
                            updateRecipe(
                                recipe = recipe,
                                picture = it,
                                localAddress = localAddress,
                                isFull = isFull
                            )
                        }
                }

            } else { // на диске нашлась, загружаем
                Log.i("bugfix: imagesDataDirector", "has saved picture, updating")
                resultRecipe = updateRecipe(
                    recipe = recipe,
                    picture = picture,
                    localAddress = localAddress,
                    isFull = isFull
                )
            }

        } else { // если запись о файле есть, берем из памяти по адресу файла
            Log.i("bugfix:imagesDataDirector", "getting local image for $fileName")
            picture = imageLoader.loadImageByFileAddress(localAddress)
            val localAddress = if (isFull) recipe.full_image.localAddress
            else recipe.pre_image.localAddress
            if (isFull)
                resultRecipe = picture?.let {
                    updateRecipe(
                        recipe = recipe,
                        picture = it,
                        localAddress = localAddress,
                        isFull = isFull
                    )
                }
        }

        imageCallback.updateRecipeItem(resultRecipe!!) // пошел коллбек с результатом
    }

//    fun saveImage(recipe: SingleRecipe, picture: Drawable): SingleRecipe {
//        val fileName = recipe.id
//        val localAddress = imageSager.saveImage(image = picture, fileName = fileName)
//        Log.i ("bugfix: ImagesDataDirector", "image was saved to $localAddress")
//        if (localAddress != NO_LOCAL_IMAGE_PATTERN) // если сохранение успешно
//        // обновляем инстанс модели с учетом файла фото
//            resultRecipe = picture?.let {
//                updateRecipe(
//                    recipe = recipe,
//                    picture = it,
//                    localAddress = localAddress,
//                    isFull = isFull
//                )
//            }
//    }

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

    private fun saveImageToStorage(picture: Drawable, pictureName: String): String {

        return NO_LOCAL_IMAGE_PATTERN
    }
}