package com.example.bioSpecies.ui.viewmodels.logic

import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.entity.PhotosNoName
import com.example.bioSpecies.data.local.list.ListStorage

class CameraLogic() {

    private val storage = ListStorage.getInstance()

    fun addMoreOneCapture() {
        storage.addMoreOneCapture()
    }

    fun getListnamesANimals(): List<String> {

        val listAnimals = storage.getListaAnimais()
        var listNames = mutableListOf<String>()

        var hashPMap : HashMap<String, String> = hashMapOf()

        for (animal in listAnimals){
            hashPMap.put(animal.nomeTradicional, animal.nomeTradicional)
        //listNames.add(animal.nomeTradicional)
        }

        for (h in hashPMap){
            listNames.add(h.value)
        }

        return listNames
    }

    fun addUnevaluatedPhoto(imageUnevaluated: Photos) {
        storage.addUnevaluatedPhoto(imageUnevaluated)
    }

    fun getListPhotosNoName(): List<PhotosNoName>  {
        return storage.getListPhotosNoName()
    }

    fun getListPhotosNoNameGame(): List<PhotosNoName> {
        return storage.getListNoNameGamePhotos()
    }

    fun removePhotoEvaluate(photosNoName: PhotosNoName) {
        storage.removePhotoEvaluateNoName(photosNoName)
    }

    fun removePhotoEvaluateNoNameGame(photosNoName: PhotosNoName) {
        storage.removePhotoEvaluateNoNameGame(photosNoName)
    }

}