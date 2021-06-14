package com.example.bioSpecies.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Semana(
    var semanaAnterior : Int,
    var desafioComum : Boolean,
    var desafioPoucoRaro : Boolean,
    var desafioRaro : Boolean,
    var desafioMuitoRaro : Boolean
) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

}