package com.bradrodgers.mnbreweries.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint

class DatabaseEntities {

    @Entity
    data class DbBreweryInfo(
        @PrimaryKey
        val name: String,
        val address: String = "",
        val cider: Boolean = false,
        val food: Boolean = false,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val state: String = "",
        val distance: Double = -10.0
    )

}