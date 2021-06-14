package com.example.bioSpecies.ui.viewmodels.logic

import android.util.Log
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.Coordenadas
import com.example.bioSpecies.data.entity.Especies
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.local.list.ListStorage
import com.example.bioSpecies.ui.utils.Extensions

class MapLogic() {

    private val storage = ListStorage.getInstance()
    private val extensions = Extensions()

    fun getListTestAnimais(): List<Especies> {
        return storage.getListTestAnimais()
    }

    fun getListEvaluationPhotos(): List<Photos> {
        return storage.getListEvaluationPhotos()
    }

    fun setAnimalSelected(animal: Animal) {
        storage.setAnimalSelected(animal)
    }

    fun getListAnimais(): List<Animal> {
        return storage.getListaAnimais()
    }

    fun atualizaListaAnimaisFilter(common: Boolean, notRare: Boolean, rare: Boolean, veryRare: Boolean, distance: Double) {

        val listaAnimais = storage.getListaAnimais()
        var listaAnimaisFilter = mutableListOf<Animal>()

        for (animal in listaAnimais){
            var listLocalizacoes = mutableListOf<Coordenadas>()
            if (distance != 0.0){
                for (localizacao in animal.localizacao){
                    if (extensions.distanceBetweenLatLng(localizacao.latitude, localizacao.longitude, storage.getLocation()!!.latitude, storage.getLocation()!!.longitude) <= distance){
                        listLocalizacoes.add(localizacao)
                    }
                }
            }else{
                listLocalizacoes = animal.localizacao as MutableList<Coordenadas>
            }


            if (listLocalizacoes.size > 0) {
                val a : Animal = animal
                a.localizacao = listLocalizacoes
                if (animal.raridade == 0 && common){
                    listaAnimaisFilter.add(a)
                }
                if (animal.raridade == 1 && notRare){
                    listaAnimaisFilter.add(a)
                }
                if (animal.raridade == 2 && rare){
                    listaAnimaisFilter.add(a)
                }
                if (animal.raridade == 3 && veryRare){
                    listaAnimaisFilter.add(a)
                }
            }

        }

        storage.updateListaAnimaisFilter(listaAnimaisFilter)
    }


    fun getListAnimaisFilter(): List<Animal> {

//        val listaAnimais = storage.getListAnimaisFilter()
//        var listaAnimaisFilter = mutableListOf<Animal>()
//
//        if(storage.getLocation() != null){
//            for (animal in listaAnimais) {
//                var listLocalizacoes = mutableListOf<Coordenadas>()
//                for (localizacao in animal.localizacao) {
//                    Log.i("mapaTeste", extensions.distanceBetweenLatLng(localizacao.latitude, localizacao.longitude, storage.getLocation()!!.latitude, storage.getLocation()!!.longitude).toString())
//                    if (extensions.distanceBetweenLatLng(localizacao.latitude, localizacao.longitude, storage.getLocation()!!.latitude, storage.getLocation()!!.longitude) <= 20.0) {
//                        listLocalizacoes.add(localizacao)
//                    }
//                }
//                if (listLocalizacoes.size > 0){
//                    animal.localizacao = listLocalizacoes
//                    listaAnimaisFilter.add(animal)
//                }
//            }
//        }else{
//            listaAnimaisFilter = listaAnimais as MutableList<Animal>
//        }

        return storage.getListAnimaisFilter()
    }

    fun getFilterCommon(): Boolean {
        return storage.getFilterCommon()
    }

    fun getFilterNotRare(): Boolean {
        return storage.getFilterNotRare()
    }

    fun getFilterRare(): Boolean {
        return storage.getFilterRare()
    }

    fun getFilterVeryRare(): Boolean {
        return storage.getFilterVeryRare()
    }

    fun setFilterCommon(checked: Boolean) {
        storage.setFilterCommon(checked)
    }

    fun setFilterNotRare(checked: Boolean) {
        storage.setFilterNotRare(checked)
    }

    fun setFilterRare(checked: Boolean) {
        storage.setFilterRare(checked)
    }

    fun setFilterVeryRare(checked: Boolean) {
        storage.setFilterVeryRare(checked)
    }

    fun getFIlterDistance(): Double {
        return storage.getFIlterDistance()
    }

    fun setFilterDistance(distance: Double) {
        storage.setFilterDistance(distance)
    }


}