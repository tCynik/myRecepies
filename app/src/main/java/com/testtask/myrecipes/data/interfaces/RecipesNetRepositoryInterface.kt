package com.testtask.myrecipes.data.interfaces

import org.json.JSONArray

interface RecipesNetRepositoryInterface {
    fun onHasResponse (jSonData: JSONArray)
}