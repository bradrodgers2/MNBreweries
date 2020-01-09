package com.bradrodgers.mnbreweries.breweryTextList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bradrodgers.mnbreweries.database.BreweryInfoDB
import com.bradrodgers.mnbreweries.database.DatabaseEntities
import com.bradrodgers.mnbreweries.database.getDatabase
import com.bradrodgers.mnbreweries.domain.BreweryInfo
import com.bradrodgers.mnbreweries.repository.Repository
import com.bradrodgers.mnbreweries.repository.getRepository
import kotlinx.coroutines.*

class BreweryListViewModel(application: Application) : AndroidViewModel(application) {



    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //private val database: BreweryInfoDB = getDatabase(application)

    private val repository = getRepository(application)

    val breweryInfoList = repository.breweryInfo

    fun saveBreweryInfo(info: List<BreweryInfo>){

        viewModelScope.launch{
            repository.saveBreweryInfo(info)
        }

    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BreweryListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BreweryListViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
