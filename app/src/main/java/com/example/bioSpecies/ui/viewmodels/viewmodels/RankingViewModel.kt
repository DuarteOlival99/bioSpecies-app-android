package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.*
import com.example.bioSpecies.ui.viewmodels.logic.CadernetaLogic
import com.example.bioSpecies.ui.viewmodels.logic.MapLogic
import com.example.bioSpecies.ui.viewmodels.logic.RankingLogic


class RankingViewModel (application: Application ) : AndroidViewModel (application) {

    private val rankingLogic = RankingLogic()


    fun atualizalistRankingNrCapturas(listRanking: MutableList<RankingNrCapturasFinal>) {
        rankingLogic.atualizalistRankingNrCapturas(listRanking)
    }

    fun getListRankingNrCapturas(): List<RankingNrCapturasFinal> {
        return rankingLogic.getListRankingNrCapturas()
    }

    fun atualizaMyNrcapturas(myNrCapturas: String) {
        rankingLogic.atualizaMyNrcapturas(myNrCapturas)
    }

    fun getMyNrCapturas() : String {
        return rankingLogic.getMyNrCapturas()
    }

    fun addRankingNrCapturas(user: RankingNrCapturasFinal) {
        rankingLogic.addRankingNrCapturas(user)
    }

}