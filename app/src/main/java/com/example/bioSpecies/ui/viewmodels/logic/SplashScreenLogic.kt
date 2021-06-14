package com.example.bioSpecies.ui.viewmodels.logic

import android.os.Handler
import android.util.Log
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.entity.PhotosNoName
import com.example.bioSpecies.data.local.list.ListStorage
import com.example.bioSpecies.data.repositories.AnimalRepository

class SplashScreenLogic(private val repository: AnimalRepository) {

    private val storage = ListStorage.getInstance()

    fun createListChallengesTest(comum: String, poucoRaro: String, raro: String, muitoRaro: String) {
        storage.createListChallengesTest(comum, poucoRaro, raro, muitoRaro)
        //storage.animaisTest()
    }

    fun setListUnevaluatedPhotos(listImages: MutableList<Photos>) {
        storage.setListUnevaluatedPhotos(listImages)
    }

    fun setListEvaluatedPhotos(listImages: MutableList<Photos>) {
        storage.setListEvaluatedPhotos(listImages)
    }

    fun setListMyUnevaluatedPhotos(listImages: MutableList<Photos>) {
        storage.setListMyUnevaluatedPhotos(listImages)
    }

    fun getAnimals(){
        Log.i("SplashActivityLogic", "entrou")
        repository.AnimalsConnection()
    }

    fun getAnimalsplaces() {
       // repository.getAnimalsplaces()
    }

    fun getObservations() {
        for (i in 0..10){
            Log.i("IteracoesApi", "iteracao nr $i")
            a(i)
        }
    }

    fun a(i: Int) {
        val duration = 1500L
        val handle = Handler()
        handle.postDelayed({
            //repository.getObservationsApi(i*50, (i+1)*50)
        }, duration)
    }

    fun verificaSemana() {
        repository.verificaSemana()
    }

    fun getEstatisticasDesafios() {
        repository.getEstatisticasDesafios()
    }

    fun getAllAnimals(): List<Animal> {
       return storage.getListaAnimais()
    }

    fun updateListAnimal(listAnimalFirebase: MutableList<Animal>) {
        storage.updateListaAnimais(listAnimalFirebase)
    }

    fun addAnimal(animal: Animal) {
        repository.addAninal(animal)
        storage.addAnimal(animal)
    }

    fun atualizaListAnimalsFirebase() {
       repository.atualizaListAnimalsFirebase()
    }

    fun atualizalistaAnimaisClasse() {
        repository.atualizaListAnimalsFirebase()
    }

    fun setListNoNamePhotos(listImages: MutableList<PhotosNoName>) {
        storage.setListNoNamePhotos(listImages)
    }

    fun setListNoNameGamePhotos(listImages: MutableList<PhotosNoName>) {
        storage.setListNoNameGamePhotos(listImages)
    }

    fun clearListChallenges() {
        storage.clearListChallenges()
    }

}
