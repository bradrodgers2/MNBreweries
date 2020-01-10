package com.bradrodgers.mnbreweries.repository

import android.content.Context
import android.location.Location
import android.os.Looper
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bradrodgers.mnbreweries.database.BreweryInfoDB
import com.bradrodgers.mnbreweries.database.DatabaseEntities
import com.bradrodgers.mnbreweries.domain.BreweryInfo
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.concurrent.thread


class Repository(private val localDatabase: BreweryInfoDB) {

    private val database = FirebaseFirestore.getInstance()

    val breweryInfo: LiveData<List<BreweryInfo>> = localDatabase.breweryInfoDao.getBreweryInfo().asDomainModel()

    init {
        Timber.i("repository created")

    }

    val currentLocation: LiveData<Location>
        get() {
            return _currentLocation
        }

    private var _currentLocation: MutableLiveData<Location> = MutableLiveData<Location>().apply {

        val location = Location("")
        location.latitude = 44.9375
        location.longitude = -93.2010

        value = location

    }

    suspend fun getLocation(context: Context){

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.create()?.apply {
            interval = 1 * DateUtils.HOUR_IN_MILLIS
            fastestInterval = 0
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val looper: Looper? = Looper.myLooper()

        withContext(Dispatchers.IO){

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return

                    var lastLocation = locationResult.lastLocation

                    if(lastLocation != null){
                        _currentLocation.postValue(lastLocation)
                    }else{
                        if(locationResult.locations.isNotEmpty()){

                            lastLocation = locationResult.locations.last()

                            _currentLocation.postValue(lastLocation)


                        }else{
                            Timber.e("no location found")
                        }
                    }

                    getOnlineBreweryInfo(lastLocation)

                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }

            Tasks.await(
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    looper
                )
            )
        }
    }


    fun getOnlineBreweryInfo(location: Location?){
        database.collection("breweries")
            .get()
            .addOnCompleteListener { queryTask ->
                queryTask.addOnCompleteListener {
                    if(it.isSuccessful){
                        if(it.result != null) {
                            val tempList = mutableListOf<BreweryInfo>()
                            for (value in it.result!!) {

                                var distance = 0.0
                                if(location != null){
                                    val geoPoint = value.data["geopoint"] as GeoPoint
                                    val breweryLocation = Location("")
                                    breweryLocation.latitude = geoPoint.latitude
                                    breweryLocation.longitude = geoPoint.longitude

                                    distance = location.distanceTo(breweryLocation).toDouble()
                                    distance /= 1609
                                }

                                tempList.add(
                                    BreweryInfo(
                                        name = value.data["name"] as String,
                                        address = value.data["address"] as String,
                                        food = value.data["food"] as Boolean,
                                        cider = value.data["cider"] as Boolean,
                                        state = value.data["state"] as String,
                                        geoPoint = value.data["geopoint"] as GeoPoint,
                                        distance = BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                                    )
                                )
                            }

                            thread {
                                localDatabase.breweryInfoDao.insertBreweryInfo(tempList.asDatabaseModel())
                            }

                        }
                    }else{
                        Timber.e("Error getting Firestore brewery info: %s", it.exception.toString())
                    }
                }
            }
    }

    suspend fun saveBreweryInfo(breweryInfo: List<BreweryInfo>){
        withContext(Dispatchers.IO){
            localDatabase.breweryInfoDao.insertBreweryInfo(breweryInfo = breweryInfo.asDatabaseModel())
        }
    }

    private fun LiveData<List<DatabaseEntities.DbBreweryInfo>>.asDomainModel(): LiveData<List<BreweryInfo>> {
        return Transformations.map(this){ data ->
            data.map{
                val lat = it.latitude
                val lng = it.longitude

                val geoPoint = GeoPoint(lat, lng)

                BreweryInfo(
                    name = it.name,
                    address = it.address,
                    cider = it.cider,
                    food = it.food,
                    geoPoint = geoPoint,
                    state = it.state,
                    distance = it.distance
                )
            }
        }
    }

    private fun List<BreweryInfo>.asDatabaseModel(): List<DatabaseEntities.DbBreweryInfo>{
        return map{

            val lat = it.geoPoint.latitude
            val lng = it.geoPoint.longitude

            DatabaseEntities.DbBreweryInfo(
                name = it.name,
                address = it.address,
                cider = it.cider,
                food = it.food,
                latitude = lat,
                longitude = lng,
                state = it.state,
                distance = it.distance
            )
        }
    }
}

/*private lateinit var INSTANCE: Repository

fun getRepository(context: Context): Repository{
    synchronized(Repository::class.java){
        //Use :: reflection to allow context to determine variable type rather than manually defining it
        if(!::INSTANCE.isInitialized){
            INSTANCE = Repository(getDatabase(context))
        }
    }
    return INSTANCE
}*/