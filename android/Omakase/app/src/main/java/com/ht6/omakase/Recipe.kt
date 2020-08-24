package com.ht6.omakase


//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR

//@Entity(tableName = "plants")
data class Recipe(
//    @PrimaryKey @ColumnInfo(name = "id") val plantId: String,
    val recipeName: String, // name of recipe
    val ingredients: String,//List<String>, // list of ingredients
    val instructions: String,//List<String>, // list of instructions
    val cookingTime: Int = 60,
//    val wateringInterval: Int = 7, // how often the plant should be watered, in days
    val imageUrl: String = "" // recipe image URL
) {

    /**
     * Determines if the plant should be watered.  Returns true if [since]'s date > date of last
     * watering + watering Interval; false otherwise.
     */
//    fun shouldBeWatered(since: Calendar, lastWateringDate: Calendar) =
//        since > lastWateringDate.apply { add(DAY_OF_YEAR, wateringInterval) }

    //override fun toString() = recipeName
}
