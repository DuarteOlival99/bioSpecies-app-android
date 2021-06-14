package com.example.bioSpecies.ui.viewmodels.logic

import com.example.bioSpecies.data.entity.CapturaItem
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.local.list.ListStorage

class ProfileLogic() {

    private val storage = ListStorage.getInstance()

    fun getCaptureHistory(): List<Photos> {
        return storage.getCaptureHistory()
    }

    fun setListEvaluatedPhotos(listImages: MutableList<Photos>) {
        storage.setListEvaluatedPhotos(listImages)
    }

}