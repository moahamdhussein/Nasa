package com.example.nasa.main

import android.app.Application
import androidx.lifecycle.*
import com.example.nasa.database.getAllDatabase
import com.example.nasa.domain.Asteroid
import com.example.nasa.network.AsteroidApiFilter
import com.example.nasa.repository.AsteroidsRepository
import com.example.nasa.repository.PicturesOfDayRepository
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getAllDatabase(application)
    private val filter = MutableLiveData(AsteroidApiFilter.SAVED)
    private val picturesOfDayRepository = PicturesOfDayRepository(database)
    private val asteroidsRepository = AsteroidsRepository(database)


    init {
        viewModelScope.launch {
            picturesOfDayRepository.refreshPictureOfDay()
            asteroidsRepository.refreshAsteroids()
        }
    }

    val picOfDay = picturesOfDayRepository.pictureOfDay
    val asteroids = Transformations.switchMap(filter) {
        when (it) {
            AsteroidApiFilter.TODAY -> asteroidsRepository.asteroidsToday
            AsteroidApiFilter.WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidsSaved
        }
    }




    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedProperty: MutableLiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid


    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }


    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }


    fun updateFilter(filter: AsteroidApiFilter) {
        this.filter.value = filter
    }

}