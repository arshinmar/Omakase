package com.ht6.omakase

import com.google.gson.Gson
import com.ht6.omakase.Recipe
import java.util.logging.Logger

/**
 * Repository module for handling data operations.
 */
class Server private constructor() {

//    fun getPlants() = plantDao.getPlants()
//
//    fun getPlant(plantId: String) = plantDao.getPlant(plantId)
//
//    fun getPlantsWithGrowZoneNumber(growZoneNumber: Int) =
//        plantDao.getPlantsWithGrowZoneNumber(growZoneNumber)


    companion object {

        // For Singleton instantiation
        @Volatile private var instance: Server? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: Server().also { instance = it }
            }

        fun getRecipe() {
            //val json = """{"title": "Kotlin Tutorial #1", "author": "bezkoder", "categories" : ["Kotlin","Basic"]}"""
//            val json = """{    "name": "Caribbean Black Bean And Fruit Salad",    "id": 372858,    "image_url": "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/37/28/58/picx8Qgqh.jpg",    "time": 15,    "nutrition": {      "calories": 55.2,      "total fat": 0.0,      "sugar": 32.0,      "sodium": 6.0,      "protein": 2.0,      "saturated fat": 0.0,      "carbohydrates": 4.0    },    "ingredients": ["Reduced sodium black beans", "Salsa", "Fresh cilantro", "Red onion", "Orange peel", "Lime juice", "Ground cumin", "Banana", "Orange", "Red leaf lettuce", "Feta cheese"],    "steps": ["Combine beans , salsa , cilantro , onion , orange peel , lime juice and cumin in medium bowl", "Gently stir in banana and orange", "Spoon onto lettuce lined small serving platter", "Sprinkle cheese on top of salad , if desired", "Squeeze additional lime juice over bananas"]  }"""
//            private val recipeName: String, // name of recipe
//            private val ingredients: String,//List<String>, // list of ingredients
//            private val instructions: String,//List<String>, // list of instructions
//            private val cookingTime: Int = 60,
//            private val imageUrl: String = "" // recipe image URL
            val json = """{    "recipeName": "Caribbean Black Bean And Fruit Salad",    "imageUrl": "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/37/28/58/picx8Qgqh.jpg",    "cookingTime": 15,       "ingredients": "Reduced sodium black beans \n\nSalsa \n\nFresh cilantro \n\nRed onion \n\nOrange peel \n\nLime juice \n\nGround cumin \n\nBanana \n\nOrange \n\nRed leaf lettuce \n\nFeta cheese",    "instructions": "Combine beans , salsa , cilantro , onion , orange peel , lime juice and cumin in medium bowl \n\nGently stir in banana and orange \n\nSpoon onto lettuce lined small serving platter \n\nSprinkle cheese on top of salad , if desired \n\nSqueeze additional lime juice over bananas"  }"""

            val gson = Gson()

            val tutorial_1: Recipe = gson.fromJson(json, Recipe::class.java)
            val LOG = Logger.getLogger("Somehwer")
            LOG.warning(tutorial_1.toString())
            //println("> From JSON String:\n" + tutorial_1)
        }
    }
}