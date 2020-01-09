package com.bradrodgers.mnbreweries

import android.app.Application
import timber.log.Timber

class MNBreweries : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}