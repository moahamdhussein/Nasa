package com.example.nasa.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nasa.database.getAllDatabase
import com.example.nasa.repository.AsteroidsRepository
import com.example.nasa.repository.PicturesOfDayRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getAllDatabase(applicationContext)
        val pictureOfDayRepository = PicturesOfDayRepository(database)
        val asteroidsRepository = AsteroidsRepository(database)
        return try {
            pictureOfDayRepository.refreshPictureOfDay()
            asteroidsRepository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}