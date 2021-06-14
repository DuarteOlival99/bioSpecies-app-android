package com.example.bioSpecies.data.entity

import java.util.*

data class FotoNomeAtribuidoGame(
    var nomeAtribuido: String,
    var nomeAtribuidoUser: String
)  {

    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "FotoNomeAtribuidoGame(nomeAtribuido='$nomeAtribuido', nomeAtribuidoUser='$nomeAtribuidoUser', uuid='$uuid')"
    }


}