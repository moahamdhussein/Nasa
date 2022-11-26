package com.example.nasa.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.nasa.Constants
import com.example.nasa.database.NasaDatabase
import com.example.nasa.database.asDomainModel
import com.example.nasa.domain.PictureOfDay
import com.example.nasa.network.Network
import com.example.nasa.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PicturesOfDayRepository(private val database: NasaDatabase) {
    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it?.asDomainModel()
        }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfDay =
                    Network.radarApi.getPictureOfDayAsync(Constants.API_KEY).await()
                database.pictureOfDayDao.insertPictureOfDay(pictureOfDay.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}