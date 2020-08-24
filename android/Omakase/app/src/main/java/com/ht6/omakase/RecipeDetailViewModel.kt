package com.ht6.omakase


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ht6.omakase.Server

//import com.google.samples.apps.sunflower.BuildConfig
//import com.google.samples.apps.sunflower.PlantDetailFragment
//import com.google.samples.apps.sunflower.data.GardenPlantingRepository
//import com.google.samples.apps.sunflower.data.PlantRepository
import kotlinx.coroutines.launch

/**
 * The ViewModel used in [PlantDetailFragment].
 */
class RecipeDetailViewModel(
//    plantRepository: PlantRepository,
//    private val gardenPlantingRepository: GardenPlantingRepository,
//    private val plantId: String
    private val recipeName: String, // name of recipe
    private val ingredients: String,//List<String>, // list of ingredients
    private val instructions: String,//List<String>, // list of instructions
    private val cookingTime: Int = 60,
    private val imageUrl: String = "" // recipe image URL
) : ViewModel() {

//    val isPlanted = gardenPlantingRepository.isPlanted(plantId)
//    val plant = plantRepository.getPlant(plantId)
//    val recipe = "pizza"
    //val recipe = Recipe("Something good", listOf("stuff", "morestuff"), listOf("cook stuff", "eat"))
    val recipe = Recipe(recipeName, ingredients, instructions, cookingTime, imageUrl)
    val test = Server.getRecipe()


//    fun addPlantToGarden() {
//        viewModelScope.launch {
//            gardenPlantingRepository.createGardenPlanting(plantId)
//        }
//    }
//
//    fun hasValidUnsplashKey() = (BuildConfig.UNSPLASH_ACCESS_KEY != "null")
companion object {

//    // For Singleton instantiation
//    @Volatile private var instance: PlantRepository? = null
//
//    fun getInstance(plantDao: PlantDao) =
//        instance ?: synchronized(this) {
//            instance ?: PlantRepository(plantDao).also { instance = it }
//        }
}
}
