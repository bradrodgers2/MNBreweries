package com.bradrodgers.mnbreweries.homepage

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bradrodgers.mnbreweries.database.BreweryInfoDB
import com.bradrodgers.mnbreweries.database.getDatabase
import com.bradrodgers.mnbreweries.repository.Repository
import com.bradrodgers.mnbreweries.repository.getRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomepageViewModel(application: Application) : AndroidViewModel(application) {
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //private val database: BreweryInfoDB = getDatabase(application)

    private val repository = getRepository(application)

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
