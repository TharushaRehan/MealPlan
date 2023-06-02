package com.example.cw2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MeasurementsDao {

    @Query("Select * from measurements")
    suspend fun getAll() : List<Measurements>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurements(vararg measurements: Measurements)

    @Query("Select measurements from measurements where mealName = :name")
    suspend fun getMeasurementsDetails(name:String):String

    @Insert
    suspend fun insertAll(vararg measurements: Measurements)

}