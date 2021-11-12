package com.e.cuppie.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {

    @GET("nearbysearch/json")
    fun getNearbyPlaces(
        @Query("key") apiKey: String,
        @Query("location") location: String,
        @Query("radius") radiusInMeters: Int,
        @Query("type") placeType: String,
    ): Call<NearbyPlacesResponse>

    companion object {
        private const val ROOT_URL = "https://maps.googleapis.com/maps/api/place/"

        fun create(): PlacesService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val converterFactory = GsonConverterFactory.create()

            val retrofitInstance = Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build()

            return retrofitInstance.create(PlacesService::class.java)
        }
    }
}
