package com.bradrodgers.mnbreweries.homepage

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bradrodgers.mnbreweries.MNBreweries
import kotlinx.coroutines.launch

class HomepageViewModel(application: Application) : AndroidViewModel(application) {
    private val viewModelJob = MNBreweries.viewModelJob!!

    private val viewModelScope = MNBreweries.viewModelScope!!

    private val repository = MNBreweries.repository!!

    val currentLocation = repository.currentLocation

    fun getLocation(context: Context){
        viewModelScope.launch {
            repository.getLocation(context)
        }
    }

    //Factory for lazy viewmodel calling
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomepageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomepageViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
