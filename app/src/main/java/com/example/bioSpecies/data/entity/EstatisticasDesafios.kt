package com.example.bioSpecies.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class EstatisticasDesafios(
    var desafioFacil : Int,
    var desafioMedios : Int,
    var desafioDificeis : Int
    ) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "EstatisticasDesafios(desafioFacil=$desafioFacil, desafioMedios=$desafioMedios, desafioDificeis=$desafioDificeis, uuid='$uuid')"
    }

}