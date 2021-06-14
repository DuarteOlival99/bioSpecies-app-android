package com.example.bioSpecies.data.entity

import android.graphics.Bitmap
import java.util.*

data class Photos(
    var title: String,
    var data : Date?,
    var url: String,
    var animalName: String,
    var listPhotoEvaluation: List<PhotoEvaluation>,
    var userID: String,
    var image: Bitmap?,
    var avaliada : Boolean,
    var resultado : Int,
    var userJaViu : Boolean
) : Comparable<Photos> {

    var uuid: String = UUID.randomUUID().toString()

    override fun compareTo(other: Photos): Int {
        return data!!.compareTo(other.data);
    }

     override fun toString(): String {
        return "Photos(title='$title', data=$data, url='$url', animalName='$animalName', listPhotoEvaluation=$listPhotoEvaluation, userID='$userID', image=$image, avaliada=$avaliada, resultado=$resultado, userJaViu=$userJaViu, uuid='$uuid')"
    }

}