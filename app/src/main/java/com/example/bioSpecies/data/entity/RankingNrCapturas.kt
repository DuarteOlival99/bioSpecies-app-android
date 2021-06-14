package com.example.bioSpecies.data.entity

import java.util.*

class RankingNrCapturas(
    var userUUID : String,
    var nrCapturas : Int
) : Comparable<RankingNrCapturas> {

    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "RankingNrCapturas(userUUID='$userUUID', nrCapturas=$nrCapturas, uuid='$uuid')"
    }

    override fun compareTo(other: RankingNrCapturas): Int {
        return nrCapturas.compareTo(other.nrCapturas);
    }

}