package com.example.bioSpecies.data.remote.responses

import com.example.bioSpecies.data.entity.Coordenadas
import com.google.gson.annotations.SerializedName

class PlacesSpeciesResponse {

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
        @SerializedName("display_name")
        val displayName = ""
        @SerializedName("name")
        val name = ""
        @SerializedName("admin_level")
        val adminLevel = 0
        @SerializedName("ancestor_place_ids")
        val ancestorPlaceIds = mutableListOf<Int>()
        @SerializedName("bbox_area")
        val bboxArea = 0f
        @SerializedName("geometry_geojson")
        val geometryGeojson = GeometryGeojson()

        class GeometryGeojson {

            @SerializedName("type")
            val type = ""
            @SerializedName("coordinates")
            val coordinates = mutableListOf<MutableList<MutableList<MutableList<Double>>>>()

            override fun toString(): String {
                return "GeometryGeojson(type='$type', coordinates=$coordinates)"
            }

        }

        @SerializedName("location")
        val location = ""
        @SerializedName("place_type")
        val placeType = 0

        override fun toString(): String {
            return "Results(id=$id, displayName='$displayName', name='$name', adminLevel=$adminLevel, ancestorPlaceIds=$ancestorPlaceIds, bboxArea=$bboxArea, geometryGeojson=$geometryGeojson, location='$location', placeType=$placeType)"
        }

    }

    override fun toString(): String {
        return "PlacesSpeciesResponse(totalResults=$totalResults, page=$page, perPage=$perPage, results=$results)"
    }

}