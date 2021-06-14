package com.example.bioSpecies.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bioSpecies.data.local.room.DataConverter
import com.example.bioSpecies.data.remote.responses.ObservationSpeciesResponse
import java.util.*

@Entity
class Animal (
    var nomeTradicional: String,
    var nomeCientifico: String,
    var classe: String,
    var taxonID : Int,
    var fotoMediumUrl: String,
    var totalObservationsResults : Int,
    var raridade : Int, // 0-> comum ; 1 -> pouco raro 2 -> raro ; 3 -> muito raro
    @TypeConverters(value = [DataConverter::class])
    var localizacao : List<Coordenadas>,
    var descoberto: Boolean
    ) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "Animal(nomeTradicional='$nomeTradicional', nomeCientifico='$nomeCientifico', classe='$classe', taxonID=$taxonID, fotoMediumUrl='$fotoMediumUrl', totalObservationsResults=$totalObservationsResults, raridade=$raridade, localizacao=$localizacao, uuid='$uuid')"
    }

}