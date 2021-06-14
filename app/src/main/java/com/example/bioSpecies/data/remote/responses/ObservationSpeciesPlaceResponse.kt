package com.example.bioSpecies.data.remote.responses

import com.google.gson.annotations.SerializedName

class ObservationSpeciesPlaceResponse {

    @SerializedName("total_results")
    val totalResults = 0
    @SerializedName("page")
    val page = 0
    @SerializedName("perPage")
    val perPage = 0
    @SerializedName("results")
    val results = mutableListOf<Results>()

    class Results {
        @SerializedName("id")
        val id = 0
        @SerializedName("location")
        val location = ""
        @SerializedName("taxon_id")
        val taxonId = 0
        @SerializedName("taxon")
        val taxon = Taxon()

        class Taxon {
            @SerializedName("id")
            val id = 0

            override fun toString(): String {
                return "Taxon(id=$id)"
            }
        }

        override fun toString(): String {
            return "Results(id=$id, location='$location', taxonId=$taxonId, taxon=$taxon)"
        }


    }

    override fun toString(): String {
        return "ObservationSpeciesPlaceResponse(totalResults=$totalResults, page=$page, perPage=$perPage, results=$results)"
    }

}