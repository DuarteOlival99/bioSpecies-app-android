package com.example.bioSpecies.data.remote.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

class ObservationSpeciesResponse {

    @SerializedName("total_results")
    val totalResults = 0
    @SerializedName("page")
    val page = 0
    @SerializedName("perPage")
    val perPage = 0
    @SerializedName("results")
    val results = mutableListOf<Results>()

    class Results {
        @SerializedName("count")
        val count = 0
        @SerializedName("taxon")
        val taxon = Taxon()

        class Taxon {
            @SerializedName("id")
            val id = 0
            @SerializedName("wikipedia_url")
            val wikipediaUrl = ""
            @SerializedName("iconic_taxon_id")
            val iconicTaxonId = 0
            @SerializedName("iconic_taxon_name")
            val iconicTaxonName = ""
            @SerializedName("is_active")
            val isActive = true
            @SerializedName("name")
            val name = ""
            @SerializedName("preferred_common_name")
            val preferredCommonName = ""
            @SerializedName("rank")
            val rank = ""
            @SerializedName("rank_level")
            val rankLevel = 0
            @SerializedName("colors")
            val colors = mutableListOf<Colors>()

            class Colors {
                @SerializedName("id")
                val id = 0
                @SerializedName("value")
                val value = ""

                override fun toString(): String {
                    return "Colors(id=$id, value='$value')"
                }
            }

            @SerializedName("conservation_status")
            val conservationStatus = ConservationStatus()

            class ConservationStatus {
                @SerializedName("place_id")
                val placeId = 0
                @SerializedName("place")
                val place = Place()

                class Place{
                    @SerializedName("id")
                    val id = 0
                    @SerializedName("name")
                    val name = ""
                    @SerializedName("display_name")
                    val displayName = ""

                    override fun toString(): String {
                        return "Place(id=$id, name='$name', displayName='$displayName')"
                    }
                }

                @SerializedName("status")
                val status = ""

                override fun toString(): String {
                    return "ConservationStatus(placeId=$placeId, place=$place, status='$status')"
                }

            }

            @SerializedName("conservation_statuses")
            val conservationStatuses = mutableListOf<ConservationStatuses>()

            class ConservationStatuses {
                @SerializedName("source_id")
                val sourceId = 0
                @SerializedName("authority")
                val authority = ""
                @SerializedName("status")
                val status = ""
                @SerializedName("status_name")
                val statusName = ""
                @SerializedName("iucn")
                val iucn = 0
                @SerializedName("geoprivacy")
                val geoprivacy = ""
                @SerializedName("place")
                val place = ConservationStatus.Place()

                override fun toString(): String {
                    return "ConservationStatuses(sourceId=$sourceId, authority='$authority', status='$status', statusName='$statusName', iucn=$iucn, geoprivacy='$geoprivacy', place=$place)"
                }
            }

            @SerializedName("default_photo")
            val defaultPhoto = DefaultPhoto()

            @Entity
            class DefaultPhoto {
                @SerializedName("id")
                val id = 0
                @SerializedName("attribution")
                val attribution = ""
                @SerializedName("license_code")
                val licenseCode = ""
                @SerializedName("url")
                val url = ""
                @SerializedName("medium_url")
                val mediumUrl = ""
                @SerializedName("square_url")
                val squareUrl = ""

                @PrimaryKey
                var uuid: String = UUID.randomUUID().toString()

                override fun toString(): String {
                    return "DefaultPhoto(id=$id, attribution='$attribution', licenseCode='$licenseCode', url='$url', mediumUrl='$mediumUrl', squareUrl='$squareUrl')"
                }

            }

            @SerializedName("establishment_means")
            val establishmentMeans = EstablishmentMeans()

            class EstablishmentMeans {
                @SerializedName("establishment_means")
                val establishmentMeans = ""
                @SerializedName("place")
                val place = ConservationStatus.Place()

                override fun toString(): String {
                    return "EstablishmentMeans(establishmentMeans='$establishmentMeans', place=$place)"
                }

            }

            @SerializedName("observations_count")
            val ObservationsCount = 0
            @SerializedName("preferred_establishment_means")
            val preferredEstablishmentMeans = ""

            override fun toString(): String {
                return "Taxon(id=$id, wikipediaUrl='$wikipediaUrl', iconicTaxonId=$iconicTaxonId, iconicTaxonName='$iconicTaxonName', isActive=$isActive, name='$name', preferredCommonName='$preferredCommonName', rank='$rank', rankLevel=$rankLevel, colors=$colors, conservationStatus=$conservationStatus, conservationStatuses=$conservationStatuses, defaultPhoto=$defaultPhoto, establishmentMeans=$establishmentMeans, ObservationsCount=$ObservationsCount, preferredEstablishmentMeans='$preferredEstablishmentMeans')"
            }

        }

        override fun toString(): String {
            return "Results(count=$count, taxon=$taxon)"
        }

    }

    override fun toString(): String {
        return "ObservationSpeciesResponse(totalResults=$totalResults, page=$page, perPage=$perPage, results=$results)"
    }


}