package com.example.bioSpecies.ui.viewmodels.logic

import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.CadernetaItem
import com.example.bioSpecies.data.entity.RankingNrCapturas
import com.example.bioSpecies.data.entity.RankingNrCapturasFinal
import com.example.bioSpecies.data.local.list.ListStorage

class RankingLogic() {
    private val storage = ListStorage.getInstance()

    fun atualizalistRankingNrCapturas(listRanking: MutableList<RankingNrCapturasFinal>) {

        listRanking.sort()
        storage.atualizalistRankingNrCapturas(listRanking)
    }

    fun getListRankingNrCapturas(): List<RankingNrCapturasFinal> {
        return storage.getlistRankingNrCapturas()
    }

    fun atualizaMyNrcapturas(myNrCapturas: String) {
        storage.atualizaMyNrcapturas(myNrCapturas)
    }

    fun getMyNrCapturas(): String {
        return storage.getMyNrCapturas()
    }

    fun addRankingNrCapturas(user: RankingNrCapturasFinal) {
        storage.addRankingNrCapturas(user)
    }
}