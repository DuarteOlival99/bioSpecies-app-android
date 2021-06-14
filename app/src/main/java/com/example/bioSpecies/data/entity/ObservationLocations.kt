package com.example.bioSpecies.data.entity

import java.util.*

class ObservationLocations (
    var taxonId: Int,
    var totalResults : Int,
    var listCoordenadas : MutableList<Coordenadas>
    ) {

        var uuid: String = UUID.randomUUID().toString()
    override fun toString(): String {
        return "ObservationLocations(taxonId=$taxonId, totalResults=$totalResults, listCoordenadas=$listCoordenadas, uuid='$uuid')"
    }

}