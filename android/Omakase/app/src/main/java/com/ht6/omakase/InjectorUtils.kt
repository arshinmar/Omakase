package com.ht6.omakase


import android.content.Context
import androidx.fragment.app.Fragment
//import com.google.samples.apps.sunflower.api.UnsplashService
//import com.google.samples.apps.sunflower.data.AppDatabase
//import com.google.samples.apps.sunflower.data.GardenPlantingRepository
//import com.google.samples.apps.sunflower.data.PlantRepository
//import com.google.samples.apps.sunflower.data.UnsplashRepository
//import com.google.samples.apps.sunflower.viewmodels.GalleryViewModelFactory
//import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModelFactory
//import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModelFactory
//import com.google.samples.apps.sunflower.viewmodels.PlantListViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

//    private fun getPlantRepository(context: Context): PlantRepository {
//        return PlantRepository.getInstance(
//            AppDatabase.getInstance(context.applicationContext).plantDao())
//    }
//
//    private fun getGardenPlantingRepository(context: Context): GardenPlantingRepository {
//        return GardenPlantingRepository.getInstance(
//            AppDatabase.getInstance(context.applicationContext).gardenPlantingDao())
//    }

//    fun provideGardenPlantingListViewModelFactory(
//        context: Context
//    ): GardenPlantingListViewModelFactory {
//        return GardenPlantingListViewModelFactory(getGardenPlantingRepository(context))
//    }
//
//    fun providePlantListViewModelFactory(fragment: Fragment): PlantListViewModelFactory {
//        return PlantListViewModelFactory(getPlantRepository(fragment.requireContext()), fragment)
//    }

    fun provideRecipeDetailViewModelFactory(
        context: Context,
        recipeName: String,
        ingredients: String,//List<String>, // list of ingredients
       instructions: String,//List<String>, // list of instructions
        cookingTime: Int,
         imageUrl: String

    ): //PlantDetailViewModelFactory {
//        return PlantDetailViewModelFactory(getPlantRepository(context),
//            getGardenPlantingRepository(context), plantId)
            RecipeDetailViewModelFactory {
        return RecipeDetailViewModelFactory(recipeName, ingredients, instructions, cookingTime, imageUrl)
    }

//    fun provideGalleryViewModelFactory(): GalleryViewModelFactory {
//        val repository = UnsplashRepository(UnsplashService.create())
//        return GalleryViewModelFactory(repository)
//    }
}
