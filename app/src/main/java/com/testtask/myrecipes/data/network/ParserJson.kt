package com.testtask.myrecipes.data.network

import android.util.Log
import com.testtask.myrecipes.data.models.SingleRecipeData
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe
import org.json.JSONArray

/**
 * Класс отвечает за парсинг ответа в формате JSONArray в промежуточную модель SingleRecipeData
 */
class ParserJson {

    fun parseJson(dataJsonArray: JSONArray): List<SingleRecipe> {
        val result = mutableListOf<SingleRecipe>()

        // перебираем массив Json, переводя каждый элемент в формат SingleRecipeData
        var index = 0
        while(index < dataJsonArray.length()) {
            val currentItem = dataJsonArray.getJSONObject(index)

            result.add(SingleRecipe(
                id = currentItem.getString("id"),
                name = currentItem.getString("name"),
                description = currentItem.getString("description"),
                headline = currentItem.getString("headline"),
                difficulty = currentItem.getInt("difficulty"),
                calories = currentItem.getString("calories"),
                fats = currentItem.getString("fats"),
                proteins = currentItem.getString("proteins"),
                carbos = currentItem.getString("carbos"),
                cookingTime = currentItem.getString("time"),
                full_image = PictureModel(currentItem.getString("image"), "", null) ,
                pre_image = PictureModel(currentItem.getString("thumb"), "", null))
                )
            index++
        }
        return result
    }


}

