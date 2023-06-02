package com.example.cw2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {

    @Query("Select * from meal")
    suspend fun getAll() : List<Meal>

    @Query("Select mealName from meal where mealName like :name")
    suspend fun getMealByNames(name:String):List<String>

    @Query("Select * from meal where mealName = :name")
    suspend fun getMealDetails(name:String):List<Meal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(vararg meal: Meal)

    @Insert
    suspend fun insertAll(vararg meal: Meal)

}