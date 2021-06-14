package com.example.bioSpecies.ui.viewmodels.logic

import android.util.Log
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.ChallengeItem
import com.example.bioSpecies.data.local.list.ListStorage
import com.example.bioSpecies.data.repositories.AnimalRepository
import java.util.*
import kotlin.collections.HashMap

class ChallengesLogic(private val repository: AnimalRepository) {

    private val storage = ListStorage.getInstance()

    fun getChallengesAtuais(): List<ChallengeItem> {
        return storage.getChallengesAtuais()
    }

    fun isDateInCurrentWeek(date: Date?): Boolean {
        val currentCalendar = Calendar.getInstance()
        val week = currentCalendar[Calendar.WEEK_OF_YEAR]
        Log.i("isDateInCurrentWeek", week.toString())
        val year = currentCalendar[Calendar.YEAR]
        val targetCalendar = Calendar.getInstance()
        targetCalendar.time = date
        val targetWeek = targetCalendar[Calendar.WEEK_OF_YEAR]
        val targetYear = targetCalendar[Calendar.YEAR]
        return week == targetWeek && year == targetYear
    }

    fun atualizaStorageAndDatabaseDesafios(i : Int) : Int{
        storage.setDesafioConcluido(i)
        repository.updateSemana(i)
        repository.atualizaEstatisticas(i) // atualiza na base de dados a estatistica dos desafios faceis com mais 1
        storage.atualizaEstatisticas(i) //atualiza na storage
        return if (i == 0){
            100
        }else{
            i * 100
        }
    }

    fun verificaFotosEvaluatedComDesafios() : Int {
        val photosEvaluated = storage.getListEvaluationPhotos()
        val listaAnimais = storage.getListaAnimais()
        val listChalenges = storage.getChallengesAtuais()
        var adicionarXP = 0

        var hashMap : HashMap<String, Animal> = HashMap<String, Animal> ()

        for (animal in listaAnimais){
            hashMap.put(animal.nomeTradicional, animal)
        }

        Collections.sort(photosEvaluated)
        Log.i("photosEvaluatedOrder", photosEvaluated.toString())

        for (photo in photosEvaluated){
            if (isDateInCurrentWeek(photo.data) && photo.resultado == 1){ //verifica se ta dentro da semana
                val animal = hashMap.get(photo.animalName)
                when (animal!!.raridade){
                    0 -> { //comum
                        if (!listChalenges[0].concluido) { //se for false
                            adicionarXP += atualizaStorageAndDatabaseDesafios(0)
                        }
                    }
                    1 -> { //pouco raro
                        if (!listChalenges[1].concluido) { //se for false
                            adicionarXP += atualizaStorageAndDatabaseDesafios(1)
                        }
                    }
                    2 -> { //raro
                        if (!listChalenges[2].concluido) { //se for false
                            adicionarXP += atualizaStorageAndDatabaseDesafios(2)
                        }
                    }
                    3 -> { //muito raro
                        if (!listChalenges[3].concluido) { //se for false
                            adicionarXP += atualizaStorageAndDatabaseDesafios(3)
                        }
                    }
                }
            }
        }

        return  adicionarXP
    }


}