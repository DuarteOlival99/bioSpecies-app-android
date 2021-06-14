package com.example.bioSpecies.data.entity

import java.util.*

data class CadernetaItem(
    var nomeEspecia: String,
    var foto : Int,
    var colunaID : Int
) {

    var uuid: String = UUID.randomUUID().toString()

}