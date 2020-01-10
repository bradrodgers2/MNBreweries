package com.bradrodgers.mnbreweries

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bradrodgers.mnbreweries.database.BreweryInfoDB
import com.bradrodgers.mnbreweries.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class MNBreweries : Application() {


    companion object{
        var context: Context? = null
        var database: BreweryInfoDB? = null
        var repository: Repository? = null
        var viewModelJob: Job? = null
        var viewModelScope: CoroutineScope? = null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        context = this

        database = Room.databaseBuilder(this,
            BreweryInfoDB::class.java,
            "breweryInfoDB")
            .fallbackToDestructiveMigration()
            .build()

        repository = Repository(database!!)

        viewModelJob = SupervisorJob()

        viewModelScope = CoroutineScope(viewModelJob!! + Dispatchers.Main)

    }
}