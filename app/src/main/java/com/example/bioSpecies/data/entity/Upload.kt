package com.example.bioSpecies.data.entity

import java.util.*

data class Upload(
    var title: String,
    var url : String,
    var animalName: String
) {

    var uuid: String = UUID.randomUUID().toString()

}