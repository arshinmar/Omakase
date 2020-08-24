package com.ht6.omakase
/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.ht6.omakase.Recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//import com.google.samples.apps.sunflower.data.GardenPlantingRepository
//import com.google.samples.apps.sunflower.data.Plant
//import com.google.samples.apps.sunflower.data.PlantRepository

/**
 * Factory for creating a [PlantDetailViewModel] with a constructor that takes a [PlantRepository]
 * and an ID for the current [Plant].
 */
class RecipeDetailViewModelFactory(
//    private val plantRepository: PlantRepository,
//    private val gardenPlantingRepository: GardenPlantingRepository,
    private val recipeName: String, // name of recipe
    private val ingredients: String,//List<String>, // list of ingredients
    private val instructions: String,//List<String>, // list of instructions
    private val cookingTime: Int = 60,
    private val imageUrl: String = "" // recipe image URL
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return PlantDetailViewModel(plantRepository, gardenPlantingRepository, plantId) as T
        return RecipeDetailViewModel(recipeName, ingredients, instructions, cookingTime, imageUrl)  as T
    }
}
