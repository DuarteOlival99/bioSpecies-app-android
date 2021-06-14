package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.Especies
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.logic.MapLogic


class MapViewModel (application: Application ) : AndroidViewModel (application) {

    private val mapLogic = MapLogic()

    fun getListTestAnimais(): List<Especies> {
        return mapLogic.getListTestAnimais()
    }

    fun getListEvaluationPhotos(): List<Photos> {
       return mapLogic.getListEvaluationPhotos()
    }

    fun setAnimalSelected(animal: Animal) {
        mapLogic.setAnimalSelected(animal)
    }

    fun getListAnimais(): List<Animal> {
        return mapLogic.getListAnimais()
    }

    fun atualizaListaAnimaisFilter(common: Boolean, notRare: Boolean, rare: Boolean, veryRare: Boolean, distance: Double) {
        mapLogic.atualizaListaAnimaisFilter(common,notRare,rare,veryRare,distance)
    }

    fun getListAnimaisFilter(): List<Animal> {
        return mapLogic.getListAnimaisFilter()
    }

    fun getFilterCommon(): Boolean {
        return mapLogic.getFilterCommon()
    }

    fun getFilterNotRare(): Boolean {
        return mapLogic.getFilterNotRare()
    }

    fun getFilterRare(): Boolean {
        return mapLogic.getFilterRare()
    }

    fun getFilterVeryRare(): Boolean {
        return mapLogic.getFilterVeryRare()
    }

    fun setFilterCommon(checked: Boolean) {
        mapLogic.setFilterCommon(checked)
    }

    fun setFilterNotRare(checked: Boolean) {
        mapLogic.setFilterNotRare(checked)
    }

    fun setFilterRare(checked: Boolean) {
        mapLogic.setFilterRare(checked)
    }

    fun setFilterVeryRare(checked: Boolean) {
        mapLogic.setFilterVeryRare(checked)
    }

    fun getFilterDistance(): Double {
        return mapLogic.getFIlterDistance()
    }

    fun setFilterDistance(distance: Double) {
        mapLogic.setFilterDistance(distance)
    }


}