package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.logic.EvaluationLogic


class EvaluationViewModel (application: Application ) : AndroidViewModel (application) {
    private val evaluationLogic = EvaluationLogic()

    fun getListUpload(userID: String): List<Photos> {
        return evaluationLogic.getListUpload(userID)
    }

    fun removePhotoEvaluate(pos: Photos) {
        evaluationLogic.removePhotoEvaluate(pos)
    }
}
