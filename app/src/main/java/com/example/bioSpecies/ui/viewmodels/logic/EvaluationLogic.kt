package com.example.bioSpecies.ui.viewmodels.logic

import android.util.Log
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.local.list.ListStorage

class EvaluationLogic() {
    private val storage = ListStorage.getInstance()

    fun getListUpload(userID: String): List<Photos> {
        var finalList = mutableListOf<Photos>()
        var list = storage.getListImagesUpload()
        Log.i("getListUpload", list.toString())
        for (i in list){
            if (i.userID != userID){
                finalList.add(i)
            }
        }
        return finalList
    }

    fun removePhotoEvaluate(pos: Photos) {
        storage.removePhotoEvaluate(pos)
    }
}
