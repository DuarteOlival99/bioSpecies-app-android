package com.example.bioSpecies.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Photo (
    val id : Int,
    val attribution : String,
    val licenseCode : String,
    val url : String,
    val mediumUrl : String,
    val squareUrl : String
    ) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "Photo(id=$id, attribution='$attribution', licenseCode='$licenseCode', url='$url', mediumUrl='$mediumUrl', squareUrl='$squareUrl', uuid='$uuid')"
    }


}