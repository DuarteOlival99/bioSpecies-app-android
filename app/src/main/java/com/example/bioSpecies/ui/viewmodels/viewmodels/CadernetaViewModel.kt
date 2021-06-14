package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.CadernetaItem
import com.example.bioSpecies.data.entity.Especies
import com.example.bioSpecies.ui.viewmodels.logic.CadernetaLogic
import com.example.bioSpecies.ui.viewmodels.logic.MapLogic


class CadernetaViewModel (application: Application ) : AndroidViewModel (application) {

    private val cadernetaLogic = CadernetaLogic()


    fun getListaCadernetaAnimais(): List<CadernetaItem> {
        return cadernetaLogic.getListaCadernetaAnimais()
    }

    fun getListaColuna(id : Int, listaAnimais: List<CadernetaItem>) : List<CadernetaItem>{
        var lista = mutableListOf<CadernetaItem>()

        for (animal in listaAnimais){
            if (animal.colunaID == id){
                lista.add(animal)
            }
        }

        return lista
    }

    fun getListaCadernetaAnimais0(listaAnimais: List<CadernetaItem>): List<CadernetaItem> {
        return getListaColuna(0, listaAnimais)
    }

    fun getListaCadernetaAnimais1(listaAnimais: List<CadernetaItem>): List<CadernetaItem> {
        return getListaColuna(1, listaAnimais)
    }

    fun getListaCadernetaAnimais2(listaAnimais: List<CadernetaItem>): List<CadernetaItem> {
        return getListaColuna(2, listaAnimais)
    }

    fun getListaCadernetaAnimais3(listaAnimais: List<CadernetaItem>): List<CadernetaItem> {
        return getListaColuna(3, listaAnimais)
    }

    fun setGroupSelect(s: String) {
        cadernetaLogic.setGroupSelect(s)
    }

    fun getAnimalSelected(): Animal? {
        return cadernetaLogic.getAnimalSelected()
    }

    fun getListaNomesClasses(): List<String> {
        return cadernetaLogic.getListaNomesClasses()
    }

    fun setClassClicked(s: String) {
        cadernetaLogic.setClassClicked(s)
    }

    fun getListaAnimal(): List<Animal> {
        return cadernetaLogic.getListaAnimal()
    }

    fun setAnimalSelected(animal: Animal) {
        cadernetaLogic.setAnimalSelected(animal)
    }

}