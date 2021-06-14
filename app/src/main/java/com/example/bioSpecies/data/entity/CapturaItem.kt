package com.example.bioSpecies.data.entity

import java.util.*

data class CapturaItem (
    var foto : Int,
    var nomeEspecie: String,
    var classificacao : String
) {

    var uuid: String = UUID.randomUUID().toString()

}