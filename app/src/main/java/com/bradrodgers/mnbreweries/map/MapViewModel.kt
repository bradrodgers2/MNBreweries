package com.bradrodgers.mnbreweries.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bradrodgers.mnbreweries.MNBreweries

class MapViewModel (application: Application) : AndroidViewModel(application){

    private val viewModelJob = MNBreweries.viewModelJob!!

    private val viewModelScope = MNBreweries.viewModelScope!!

    private val repository = MNBreweries.repository!!
    val currentLocation = repository.currentLocation

    val breweryInfo = repository.breweryInfo


    //Factory for lazy viewmodel calling
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }


}
