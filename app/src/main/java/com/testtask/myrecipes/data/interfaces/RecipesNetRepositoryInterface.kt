package com.testtask.myrecipes.data.interfaces

import org.json.JSONArray

interface RecipesNetRepositoryInterface {
    fun makeRequest(URLRequest: String): JSONArray?
}