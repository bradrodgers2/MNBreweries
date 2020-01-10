package com.bradrodgers.mnbreweries.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BreweryInfoDao {

    //Can add additional queries here to filter info, for now, grabbing the whole list is fine
    @Query("select * from dbbreweryinfo")
    fun getBreweryInfo(): LiveData<List<DatabaseEntities.DbBreweryInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBreweryInfo(breweryInfo: List<DatabaseEntities.DbBreweryInfo>)

    @Query("update dbbreweryinfo set distance = :distance where name = :name")
    fun updateDistance(distance: Double, name: String)

}

@Database(entities = [DatabaseEntities.DbBreweryInfo::class], version = 2, exportSchema = false)
abstract class BreweryInfoDB: RoomDatabase(){
    abstract val breweryInfoDao: BreweryInfoDao
}

/*private lateinit var INSTANCE: BreweryInfoDB

fun getDatabase(context: Context): BreweryInfoDB{
    synchronized(BreweryInfoDB::class.java){
        //Use :: reflection to allow context to determine variable type rather than manually defining it
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                BreweryInfoDB::class.java,
                "breweryInfoDB")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}*/