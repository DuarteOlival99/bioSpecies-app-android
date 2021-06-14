package com.example.bioSpecies.data.entity

import java.util.*

class ChallengeItem (
    var concluido : Boolean,
    var descricao : String,
    var dificuldadeImagem : Int,
    var dificuldade: Int
    ) {

        var uuid: String = UUID.randomUUID().toString()

    }