package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.Especies
import com.example.bioSpecies.data.entity.EstatisticasDesafios
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.logic.EstatitisticasLogic
import com.example.bioSpecies.ui.viewmodels.logic.MapLogic


class EstatisticasViewModel (application: Application ) : AndroidViewModel (application) {

    private val estatitisticasLogic = EstatitisticasLogic()

    fun getCaptureEstatics(): Int {
        return estatitisticasLogic.getCaptureEstatics()
    }

    fun atualizaCaptureEstatisticas(){
        estatitisticasLogic.atualizaCaptureEstatisticas()
    }

    fun getEstatisticasDesafios(): EstatisticasDesafios {
        return estatitisticasLogic.getEstatisticasDesafios()
    }

}