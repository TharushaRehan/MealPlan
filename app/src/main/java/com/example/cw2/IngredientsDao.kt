package com.example.cw2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IngredientsDao {

    @Query("Select * from ingredients")
    suspend fun getAll() : List<Ingredients>

    @Query("Select mealName from ingredients where ingredients like :name OR ingredients = :name")
    suspend fun getMealByIngredients(name:String):List<String>

    @Query("Select ingredients from ingredients where mealName = :name")
    suspend fun getIngredientsDetails(name:String):String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(vararg ingredients : Ingredients)

    @Insert
    suspend fun insertAll(vararg ingredients: Ingredients)
}