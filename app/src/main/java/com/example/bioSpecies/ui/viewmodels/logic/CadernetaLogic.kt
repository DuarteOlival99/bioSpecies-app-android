package com.example.bioSpecies.ui.viewmodels.logic

import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.CadernetaItem
import com.example.bioSpecies.data.local.list.ListStorage

class CadernetaLogic() {
    private val storage = ListStorage.getInstance()



    fun getListaCadernetaAnimais(): List<CadernetaItem> {
        return storage.getListaCadernetaAnimais()
    }

    fun setGroupSelect(s: String) {
        storage.setGroupSelect(s)
    }

    fun getAnimalSelected(): Animal? {
        return storage.getAnimalSelected()
    }

    fun getListaNomesClasses(): List<String> {
        return storage.getListaNomesClasses()
    }

    fun setClassClicked(s: String) {
        storage.setClassClicked(s)
    }

    fun getListaAnimal(): List<Animal> {

        val lista = storage.getListaAnimaisClasse()
        var listaClasse = mutableListOf<Animal>()
        val classeSelecionada = storage.getClasseSelected()

        for (classe in lista){ //percorre a lista das classes
            if (classe.classe == classeSelecionada){ //verifica qual e igual a classe carregada
                listaClasse = classe.listAnimais // passa a lista da classe para a variavel listaClasse
                break
            }
        }

        return listaClasse
    }

    fun setAnimalSelected(animal: Animal) {
        storage.setAnimalSelectedCaderneta(animal)
    }


}