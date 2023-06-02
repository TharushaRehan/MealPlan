package com.example.cw2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Meal::class,Ingredients::class,Measurements::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao() : MealDao
    abstract fun ingredientsDao() : IngredientsDao
    abstract fun measurementsDao() : MeasurementsDao
}