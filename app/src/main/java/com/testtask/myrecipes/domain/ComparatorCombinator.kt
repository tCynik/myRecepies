package com.testtask.myrecipes.domain

import com.testtask.myrecipes.domain.models.SingleRecipe
import java.util.*

class ComparatorCombinator {
    fun compareAndCombineMaps(comparableMap: SortedMap<String, SingleRecipe>, updaterMap: SortedMap<String, SingleRecipe>): SortedMap<String, SingleRecipe> {
        val iterator = updaterMap.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next().key
            val currentRecipe = updaterMap[key]

            if (comparableMap.containsKey(key) && comparableMap[key]!!.equals(updaterMap[key])) break
            else comparableMap[key] = currentRecipe // обновляем этот конкретный рецемр
        }
        return comparableMap
    }
}