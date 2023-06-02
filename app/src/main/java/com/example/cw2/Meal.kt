package com.example.cw2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Meal(

    @PrimaryKey val mealName : String,
    val drink : String?,
    val category : String,
    val area : String,
    val instructions : String,
    val mealThumb : String?,
    val tags : String?,
    val youtube : String?,
    val source : String?,
    val imgSource : String?,
    val CreativeCommonsConfirmed : String?,
    val dateModified : String?

)
