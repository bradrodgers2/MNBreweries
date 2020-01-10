package com.bradrodgers.mnbreweries.breweryTextList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bradrodgers.mnbreweries.MNBreweries
import com.bradrodgers.mnbreweries.domain.BreweryInfo
import kotlinx.coroutines.launch

class BreweryListViewModel(application: Application) : AndroidViewModel(application) {



    private val viewModelJob = MNBreweries.viewModelJob!!

    private val viewModelScope = MNBreweries.viewModelScope!!

    //private val database: BreweryInfoDB = getDatabase(application)

    private val repository = MNBreweries.repository!!

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
