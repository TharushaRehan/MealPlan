package com.example.cw2

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
    data class Ingredients(
    @PrimaryKey val mealName : String,
    val ingredients : String,

)

