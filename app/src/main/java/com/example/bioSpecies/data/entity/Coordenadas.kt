package com.example.bioSpecies.data.entity

import java.util.*

class Coordenadas(
    var latitude: Double,
    var longitude: Double

) {

    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "Coordenadas(latitude=$latitude, longitude=$longitude)"
    }

}