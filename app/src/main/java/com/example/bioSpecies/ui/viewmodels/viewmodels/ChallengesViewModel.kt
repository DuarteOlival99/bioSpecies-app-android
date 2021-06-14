package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.ChallengeItem
import com.example.bioSpecies.data.local.room.BioSpeciesDatabase
import com.example.bioSpecies.data.remote.RetrofitBuilder
import com.example.bioSpecies.data.repositories.AnimalRepository
import com.example.bioSpecies.ui.viewmodels.logic.ChallengesLogic
import com.example.bioSpecies.ui.viewmodels.logic.MapLogic
import com.example.bioSpecies.ui.viewmodels.logic.SplashScreenLogic

class ChallengesViewModel (application: Application ) : AndroidViewModel (application) {

    private val storage = BioSpeciesDatabase.getInstance(application).operationDao()
    private val repository = AnimalRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val challengesLogic = ChallengesLogic(repository)

    fun getChallengesAtuais(): List<ChallengeItem> {
        return challengesLogic.getChallengesAtuais()
    }

    fun verificaFotosEvaluatedComDesafios() : Int {
        return challengesLogic.verificaFotosEvaluatedComDesafios()
    }


}