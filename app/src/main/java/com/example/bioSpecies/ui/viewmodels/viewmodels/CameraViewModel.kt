package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.entity.PhotosNoName
import com.example.bioSpecies.ui.viewmodels.logic.AboutLogic
import com.example.bioSpecies.ui.viewmodels.logic.CameraLogic


class CameraViewModel (application: Application ) : AndroidViewModel (application) {

    private val cameraLogic = CameraLogic()

    fun addMoreOneCapture() {
        cameraLogic.addMoreOneCapture()
    }

    fun getlListAnimalsNames(): List<String> {
        return cameraLogic.getListnamesANimals()
    }

    fun addUnevaluatedPhoto(imageUnevaluated: Photos) {
        cameraLogic.addUnevaluatedPhoto(imageUnevaluated)
    }

    fun getListPhotosNoName(): List<PhotosNoName> {
        return cameraLogic.getListPhotosNoName()
    }

    fun getListPhotosNoNameGame(): List<PhotosNoName> {
        return cameraLogic.getListPhotosNoNameGame()
    }

    fun removePhotoEvaluate(photosNoName: PhotosNoName) {
        cameraLogic.removePhotoEvaluate(photosNoName)
    }

    fun removePhotoEvaluateNoNameGame(photosNoName: PhotosNoName) {
        cameraLogic.removePhotoEvaluateNoNameGame(photosNoName)
    }
}