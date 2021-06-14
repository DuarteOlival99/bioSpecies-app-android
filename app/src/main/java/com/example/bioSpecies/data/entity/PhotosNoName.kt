package com.example.bioSpecies.data.entity

import android.graphics.Bitmap
import java.util.*

data class PhotosNoName(
    var title: String,
    var data : Date?,
    var url: String,
    var listAnimalName: List<FotoNomeAtribuido>,
    var listAnimalNameGame: List<FotoNomeAtribuidoGame>,
    var userID: String,
    var image: Bitmap?,
    var avaliada : Boolean,
    var resultado : Int,
    var userJaViu : Boolean
) : Comparable<PhotosNoName> {

    var uuid: String = UUID.randomUUID().toString()

    override fun compareTo(other: PhotosNoName): Int {
        return data!!.compareTo(other.data);
    }

    override fun toString(): String {
        return "PhotosNoName(title='$title', data=$data, url='$url', listAnimalName=$listAnimalName, userID='$userID', image=$image, avaliada=$avaliada, resultado=$resultado, userJaViu=$userJaViu, uuid='$uuid')"
    }
}