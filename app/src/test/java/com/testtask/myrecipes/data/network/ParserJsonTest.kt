package com.testtask.myrecipes.data.network

import com.testtask.myrecipes.data.models.SingleRecipeData
import junit.framework.Assert
import org.json.JSONArray
import org.junit.Test

internal class ParserJsonTest {
    val parser = ParserJson()
    val json = JSONArray("[\n" +
            "  {\n" +
            "    \"calories\": \"516 kcal\",\n" +
            "    \"carbos\": \"47 g\",\n" +
            "    \"description\": \"There\\u2019s nothing like the simple things in life - the smell of freshly cut grass, sitting outside on a nice sunny day, spending time with friends and family. Well here is a recipe that delivers simple culinary pleasures - some nice fresh fish with a crispy crust, crunchy potato wedges and some delightfully sweet sugar snap peas flavoured with cooling mint. Slip into something comfortable and relax into a delicious dinner!\",\n" +
            "    \"difficulty\": 0,\n" +
            "    \"fats\": \"8 g\",\n" +
            "    \"headline\": \"with Sweet Potato Wedges and Minted Snap Peas\",\n" +
            "    \"id\": \"533143aaff604d567f8b4571\",\n" +
            "    \"image\": \"https://img.hellofresh.com/f_auto,q_auto/hellofresh_s3/image/533143aaff604d567f8b4571.jpg\",\n" +
            "    \"name\": \"Crispy Fish Goujons \",\n" +
            "    \"proteins\": \"43 g\",\n" +
            "    \"thumb\": \"https://img.hellofresh.com/f_auto,q_auto,w_300/hellofresh_s3/image/533143aaff604d567f8b4571.jpg\",\n" +
            "    \"time\": \"PT35M\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"calories\": \"397 kcal\",\n" +
            "    \"carbos\": \"26 g\",\n" +
            "    \"description\": \"Close your eyes, open up your Ras El Hanout and inhale deeply. You are no longer standing in your kitchen. Around you are the sounds of a bustling market. Robed men sell ornate carpets and a camel nibbles affectionately at your ear.  OK, we\\u2019re pretty sure Paul McKenna\\u2019s job is safe for now, but get cooking this recipe and take dinnertime on a magic carpet ride to Casablanca! Our tip for this recipe is to take your meat out of the fridge at least 30 minutes before dinner which will allow you to cook it more evenly.\",\n" +
            "    \"difficulty\": 0,\n" +
            "    \"fats\": \"5 g\",\n" +
            "    \"headline\": \"with Spinach and Lemon Couscous\",\n" +
            "    \"id\": \"53314247ff604d44808b4569\",\n" +
            "    \"image\": \"https://img.hellofresh.com/f_auto,q_auto/hellofresh_s3/image/53314247ff604d44808b4569.jpg\",\n" +
            "    \"name\": \"Moroccan Skirt Steak \",\n" +
            "    \"proteins\": \"31 g\",\n" +
            "    \"thumb\": \"https://img.hellofresh.com/f_auto,q_auto,w_300/hellofresh_s3/image/53314247ff604d44808b4569.jpg\",\n" +
            "    \"time\": \"PT30M\"\n" +
            "  }]")

    val expectedResult = mutableListOf<SingleRecipeData>(
        SingleRecipeData(
            id = "533143aaff604d567f8b4571",
            name = "Crispy Fish Goujons ",
            description = "There\\u2019s nothing like the simple things in life - the smell of freshly cut grass, sitting outside on a nice sunny day, spending time with friends and family. Well here is a recipe that delivers simple culinary pleasures - some nice fresh fish with a crispy crust, crunchy potato wedges and some delightfully sweet sugar snap peas flavoured with cooling mint. Slip into something comfortable and relax into a delicious dinner!",
            headline = "with Sweet Potato Wedges and Minted Snap Peas",
            difficulty = 0,
            calories = "516 kcal",
            fats = "8 g",
            proteins = "43 g",
            carbos = "47 g",
            cookingTime = "PT35M",
            full_image_address = "https://img.hellofresh.com/f_auto,q_auto/hellofresh_s3/image/53314247ff604d44808b4569.jpg",
            pre_image_address = "https://img.hellofresh.com/f_auto,q_auto,w_300/hellofresh_s3/image/53314247ff604d44808b4569.jpg"),
        SingleRecipeData(
            id = "53314247ff604d44808b4569",
            name = "Moroccan Skirt Steak ",
            description = "There\\u2019s nothing like the simple things in life - the smell of freshly cut grass, sitting outside on a nice sunny day, spending time with friends and family. Well here is a recipe that delivers simple culinary pleasures - some nice fresh fish with a crispy crust, crunchy potato wedges and some delightfully sweet sugar snap peas flavoured with cooling mint. Slip into something comfortable and relax into a delicious dinner!",
            headline = "with Spinach and Lemon Couscous",
            difficulty = 0,
            calories = "516 kcal",
            fats = "5 g",
            proteins = "31 g",
            carbos = "26 g",
            cookingTime = "PT30M",
            full_image_address = "https://img.hellofresh.com/f_auto,q_auto/hellofresh_s3/image/53314247ff604d44808b4569.jpg",
            pre_image_address = "https://img.hellofresh.com/f_auto,q_auto,w_300/hellofresh_s3/image/53314247ff604d44808b4569.jpg")
    )
//    val firstItem = SingleRecipeData(
//        id = "533143aaff604d567f8b4571",
//        name = "Crispy Fish Goujons ",
//        description = "There\\u2019s nothing like the simple things in life - the smell of freshly cut grass, sitting outside on a nice sunny day, spending time with friends and family. Well here is a recipe that delivers simple culinary pleasures - some nice fresh fish with a crispy crust, crunchy potato wedges and some delightfully sweet sugar snap peas flavoured with cooling mint. Slip into something comfortable and relax into a delicious dinner!",
//        headline = "with Sweet Potato Wedges and Minted Snap Peas",
//        difficulty = 0,
//        calories = "516 kcal",
//        fats = "8 g",
//        proteins = "43 g",
//        carbos = "47 g",
//        cookingTime = "PT35M",
//        full_image_address = "https://img.hellofresh.com/f_auto,q_auto/hellofresh_s3/image/53314247ff604d44808b4569.jpg",
//        pre_image_address = "https://img.hellofresh.com/f_auto,q_auto,w_300/hellofresh_s3/image/53314247ff604d44808b4569.jpg")
//
//    val secondItem = SingleRecipeData(
//        id = "53314247ff604d44808b4569",
//        name = "Moroccan Skirt Steak ",
//        description = "There\\u2019s nothing like the simple things in life - the smell of freshly cut grass, sitting outside on a nice sunny day, spending time with friends and family. Well here is a recipe that delivers simple culinary pleasures - some nice fresh fish with a crispy crust, crunchy potato wedges and some delightfully sweet sugar snap peas flavoured with cooling mint. Slip into something comfortable and relax into a delicious dinner!",
//        headline = "with Spinach and Lemon Couscous",
//        difficulty = 0,
//        calories = "516 kcal",
//        fats = "5 g",
//        proteins = "31 g",
//        carbos = "26 g",
//        cookingTime = "PT30M",
//        full_image_address = "https://img.hellofresh.com/f_auto,q_auto/hellofresh_s3/image/53314247ff604d44808b4569.jpg",
//        pre_image_address = "https://img.hellofresh.com/f_auto,q_auto,w_300/hellofresh_s3/image/53314247ff604d44808b4569.jpg")


    @Test
    fun parseJsonTwoItems(){
        val result = parser.parseJson(json)
        Assert.assertEquals(expectedResult, result)
    }

}