package com.testtask.myrecipes.data.network

import android.util.Log
import com.testtask.myrecipes.data.network.models.ResponseAbstract
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe
import org.json.JSONException
import java.util.*

/**
 * Класс отвечает за парсинг ответа в формате JSONArray в промежуточную модель SingleRecipeData
 */
private const val NO_LOCAL_IMAGE_PATTERN = "EMPTY"
class ParserJson {

    fun parseJson(responseToParsing: ResponseAbstract): SortedMap<String, SingleRecipe> {
        val dataJsonArray = responseToParsing.getData()!!
        val result: SortedMap<String, SingleRecipe> = sortedMapOf()

        // перебираем массив Json, переводя каждый элемент в формат SingleRecipeData
        var index = 0
        while(index < dataJsonArray.length()) {
            val currentItem = dataJsonArray.getJSONObject(index)

            try{ // обработка ошибок в случае возврата ответа с другой структурой Json
                val id = currentItem.getString("id")
                result[id] = SingleRecipe(id = id,
                    name = currentItem.getString("name"),
                    description = currentItem.getString("description"),
                    headline = currentItem.getString("headline"),
                    difficulty = currentItem.getInt("difficulty"),
                    calories = currentItem.getString("calories"),
                    fats = currentItem.getString("fats"),
                    proteins = currentItem.getString("proteins"),
                    carbos = currentItem.getString("carbos"),
                    cookingTime = currentItem.getString("time"),
                    full_image = PictureModel(currentItem.getString("image"), NO_LOCAL_IMAGE_PATTERN, null) ,
                    pre_image = PictureModel(currentItem.getString("thumb"), NO_LOCAL_IMAGE_PATTERN, null))
//                result.add(SingleRecipe(
//                    id = id,
//                    name = currentItem.getString("name"),
//                    description = currentItem.getString("description"),
//                    headline = currentItem.getString("headline"),
//                    difficulty = currentItem.getInt("difficulty"),
//                    calories = currentItem.getString("calories"),
//                    fats = currentItem.getString("fats"),
//                    proteins = currentItem.getString("proteins"),
//                    carbos = currentItem.getString("carbos"),
//                    cookingTime = currentItem.getString("time"),
//                    full_image = PictureModel(currentItem.getString("image"), NO_LOCAL_IMAGE_PATTERN, null) ,
//                    pre_image = PictureModel(currentItem.getString("thumb"), NO_LOCAL_IMAGE_PATTERN, null))
//                )
            } catch (e: JSONException) {
                Log.i("bugfix: parser ", "parsing exception occurred $e")
            }

            index++
        }
        return result
    }


}

