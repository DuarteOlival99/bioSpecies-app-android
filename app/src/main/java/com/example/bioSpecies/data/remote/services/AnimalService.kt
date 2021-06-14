package com.example.bioSpecies.data.remote.services

import com.example.bioSpecies.data.remote.responses.ObservationSpeciesPlaceResponse
import com.example.bioSpecies.data.remote.responses.ObservationSpeciesResponse
import com.example.bioSpecies.data.remote.responses.PlacesSpeciesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface AnimalService {


    @GET("observations/species_counts")
    suspend fun getObservationSpecies(@Query("place_id") place_id: Int,
                                      @Query("per_page") per_page: String): Response<ObservationSpeciesResponse>

    @GET("places/{id}")
    suspend fun getPlacesSpecies(@Path("id") id: String): Response<PlacesSpeciesResponse>

    @GET("observations")
    suspend fun getObservationPlace(@Query("taxon_id") taxon_id: String,
                                    @Query("place_id") place_id: Int,
                                    @Query("verifiable") verifiable: Boolean,
                                    @Query("page") page: String,
                                    @Query("per_page") per_page: String,
                                    @Query("order_by") order_by: String): Response<ObservationSpeciesPlaceResponse>

}