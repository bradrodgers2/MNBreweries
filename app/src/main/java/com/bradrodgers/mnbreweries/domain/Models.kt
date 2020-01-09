package com.bradrodgers.mnbreweries.domain

import com.google.firebase.firestore.GeoPoint

data class BreweryInfo(
    val name: String = "",
    val address: String = "",
    val cider: Boolean = false,
    val food: Boolean = false,
    val geoPoint: GeoPoint,
    val state: String = "",
    val distance: Double = -10.0
)