package com.example.bioSpecies.data.entity

import com.example.bioSpecies.data.remote.responses.ObservationSpeciesResponse
import java.util.*

class AnimalClasse (
    var classe: String,
    var listAnimais : MutableList<Animal>
    ) {

        var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "AnimalClasse(classe='$classe', listAnimais=$listAnimais, uuid='$uuid')"
    }

}