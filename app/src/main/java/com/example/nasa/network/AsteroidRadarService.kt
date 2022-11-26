package com.example.nasa.network

import com.example.nasa.Constants.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


enum class AsteroidApiFilter(val value: String) { SAVED("saved"), TODAY("today"), WEEK("week") }

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()


interface AsteroidRadarService {
    @GET(value = "planetary/apod")
    fun getPictureOfDayAsync(@Query("api_key") apiKey: String): Deferred<NetworkPictureOfDay>

    @GET(value = "neo/rest/v1/feed")
    fun getAsteroidsAsync(@Query("api_key") apiKey: String): Deferred<String>
}


object Network {

    val radarApi = retrofit.create(AsteroidRadarService::class.java)
}