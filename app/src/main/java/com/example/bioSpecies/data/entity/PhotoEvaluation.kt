package com.example.bioSpecies.data.entity

import java.util.*

data class PhotoEvaluation(
    var evaluation: String,
    var evaluationUser: String
)  {

    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "PhotoEvaluation(evaluation='$evaluation', evaluationUser='$evaluationUser', uuid='$uuid')"
    }


}