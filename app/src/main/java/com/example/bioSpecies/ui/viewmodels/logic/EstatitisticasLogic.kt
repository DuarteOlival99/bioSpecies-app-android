package com.example.bioSpecies.ui.viewmodels.logic

import com.example.bioSpecies.data.entity.Especies
import com.example.bioSpecies.data.entity.EstatisticasDesafios
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.local.list.ListStorage

class EstatitisticasLogic() {

    private val storage = ListStorage.getInstance()

    fun getCaptureEstatics(): Int {
        return storage.getCaptureEstatics()
    }

    fun atualizaCaptureEstatisticas() {
        storage.atualizaCaptureEstatisticas()
    }

    fun getEstatisticasDesafios(): EstatisticasDesafios {
       return storage.getEstatisticasDesafios()
    }
}