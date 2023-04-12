package com.testtask.myrecipes.domain

import android.util.Log
import com.testtask.myrecipes.domain.models.SingleRecipe
import java.util.*

/**
 * класс для сравнения мап и для их комбинирования если обнаружена разница
 * возвращает комбинированную мапу по принципу:
 *  - если в comparableMap не обнаружен такой ключ, как в updaterMap добавляем его в мапу
 *  - если в записи с таким ключом обнаруежено значение, отличное от updaterMap, переписываем значение
 *  - если мапы иденитчные, возвращаем налл
 */
class ComparatorCombinator {
    fun compareAndCombineMaps(comparableMap: SortedMap<String, SingleRecipe>, updaterMap: SortedMap<String, SingleRecipe>): SortedMap<String, SingleRecipe>? {
        var isMapsEquals = true
        val iterator = updaterMap.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next().key
            val currentRecipe = updaterMap[key]

            if (comparableMap.containsKey(key) && comparableMap[key]!!.equals(updaterMap[key])) break
            else {
                isMapsEquals = false
                comparableMap[key] = currentRecipe // обновляем этот конкретный рецемр
            }
        }
        Log.i("bugfix - ComparatorCombinator", "maps was compared. the maps is equals = $isMapsEquals")
        return if (isMapsEquals) null else return comparableMap
    }
}