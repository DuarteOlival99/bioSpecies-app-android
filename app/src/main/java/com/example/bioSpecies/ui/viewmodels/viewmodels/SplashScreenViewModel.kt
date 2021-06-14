package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.entity.PhotosNoName
import com.example.bioSpecies.data.local.room.BioSpeciesDatabase
import com.example.bioSpecies.data.remote.RetrofitBuilder
import com.example.bioSpecies.data.repositories.AnimalRepository
import com.example.bioSpecies.ui.viewmodels.logic.SplashScreenLogic


const val ENDPOINT = "https://api.inaturalist.org/v1/"

class SplashScreenViewModel ( application: Application ) : AndroidViewModel (application) {

    private val storage = BioSpeciesDatabase.getInstance(application).operationDao()
    private val repository = AnimalRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val splashScreenLogic = SplashScreenLogic(repository)


    fun createListChallengesTest(comum: String, poucoRaro: String, raro: String, muitoRaro: String) {
        splashScreenLogic.createListChallengesTest(comum, poucoRaro, raro, muitoRaro)
    }

    fun setListUnevaluatedPhotos(listImages: MutableList<Photos>) {
        splashScreenLogic.setListUnevaluatedPhotos(listImages)
    }

    fun setListEvaluatedPhotos(listImages: MutableList<Photos>) {
        splashScreenLogic.setListEvaluatedPhotos(listImages)
    }

    fun setListMyUnevaluatedPhotos(listImages: MutableList<Photos>) {
        splashScreenLogic.setListMyUnevaluatedPhotos(listImages)
    }

    fun getAnimals(){
        Log.i("SplashActivityViewModel", "entrou")
        splashScreenLogic.getAnimals()
    }

    fun getAnimalsplaces() {
        splashScreenLogic.getAnimalsplaces()
    }

    fun getObservations() {
        splashScreenLogic.getObservations()
    }

    fun verificaSemana() {
        splashScreenLogic.verificaSemana()
    }

    fun getEstatisticasDesafios() {
        splashScreenLogic.getEstatisticasDesafios()
    }

    fun getAllAnimals(): List<Animal> {
        return splashScreenLogic.getAllAnimals()
    }

    fun updateListAnimal(listAnimalFirebase: MutableList<Animal>) {
        splashScreenLogic.updateListAnimal(listAnimalFirebase)
    }

    fun addAnimal(animal: Animal) {
        splashScreenLogic.addAnimal(animal)
    }

    fun atualizaListAnimalsFirebase() {
        splashScreenLogic.atualizaListAnimalsFirebase()
    }

    fun atualizalistaAnimaisClasse() {
        splashScreenLogic.atualizalistaAnimaisClasse()
    }

    fun setListNoNamePhotos(listImages: MutableList<PhotosNoName>) {
        splashScreenLogic.setListNoNamePhotos(listImages)
    }

    fun setListNoNameGamePhotos(listImages: MutableList<PhotosNoName>) {
        splashScreenLogic.setListNoNameGamePhotos(listImages)
    }

    fun clearListChallenges() {
        splashScreenLogic.clearListChallenges()
    }

}