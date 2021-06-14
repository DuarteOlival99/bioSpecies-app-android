package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.CapturaItem
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.logic.MapLogic
import com.example.bioSpecies.ui.viewmodels.logic.ProfileLogic


class ProfileViewModel (application: Application ) : AndroidViewModel (application) {

    private val profileLogic = ProfileLogic()

    fun getCaptureHistory(): List<Photos> {
        return profileLogic.getCaptureHistory()
    }

    fun setListEvaluatedPhotos(listImages: MutableList<Photos>) {
        profileLogic.setListEvaluatedPhotos(listImages)
    }
}