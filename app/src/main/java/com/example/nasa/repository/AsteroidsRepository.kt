package com.example.nasa.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

import com.example.nasa.domain.Asteroid
import com.example.nasa.Constants
import com.example.nasa.api.parseAsteroidsJsonResult
import com.example.nasa.database.NasaDatabase
import com.example.nasa.database.asDomainModel
import com.example.nasa.network.Network
import com.example.nasa.network.asDatabaseModel
import com.example.nasa.Utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber;
import java.util.*

class AsteroidsRepository(private val database:NasaDatabase) {
    val today = Utils.convertDateStringToFormattedString(Calendar.getInstance().time, Constants.API_QUERY_DATE_FORMAT)
    val week = Utils.convertDateStringToFormattedString(
        Utils.addDaysToDate(Calendar.getInstance().time, 7),
        Constants.API_QUERY_DATE_FORMAT)

    val asteroidsSaved: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }
    val asteroidsWeek: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeeklyAsteroids(today,week)) {
            it.asDomainModel()
        }
    val asteroidsToday: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodayAsteroids(today)) {
            it.asDomainModel()
        }


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            // Try statement is used to catch Network Exceptions so the app does not
            // crash when attempting to load without a network connection
            try {
                val asteroids = Network.radarApi.getAsteroids(Constants.API_KEY).await()
                database.asteroidDao.insertAll(*parseAsteroidsJsonResult(JSONObject(asteroids)).asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}