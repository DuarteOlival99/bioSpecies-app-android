package com.example.bioSpecies.data.entity

import java.util.*

class RankingNrCapturasFinal(
    var userUUID : String,
    var nrCapturas : Int,
    var url : String
    ) : Comparable<RankingNrCapturasFinal>
 {

    var uuid: String = UUID.randomUUID().toString()

     override fun compareTo(other: RankingNrCapturasFinal): Int {
         return nrCapturas.compareTo(other.nrCapturas);
     }

     override fun toString(): String {
         return "RankingNrCapturasFinal(userUUID='$userUUID', nrCapturas=$nrCapturas, url='$url', uuid='$uuid')"
     }

 }