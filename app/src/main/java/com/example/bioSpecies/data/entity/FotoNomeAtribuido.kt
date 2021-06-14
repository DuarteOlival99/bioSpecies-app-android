package com.example.bioSpecies.data.entity

import java.util.*

data class FotoNomeAtribuido(
    var nomeAtribuido: String,
    var nomeAtribuidoUser: String
)  {

    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "FotoNomeAtribuido(nomeAtribuido='$nomeAtribuido', nomeAtribuidoUser='$nomeAtribuidoUser', uuid='$uuid')"
    }


}