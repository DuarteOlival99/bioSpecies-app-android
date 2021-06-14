package com.example.bioSpecies.data.repositories

import android.util.Log
import com.example.bioSpecies.data.entity.*
import com.example.bioSpecies.data.local.list.ListStorage
import com.example.bioSpecies.data.local.room.dao.OperationDao
import com.example.bioSpecies.data.remote.responses.ObservationSpeciesPlaceResponse
import com.example.bioSpecies.data.remote.responses.ObservationSpeciesResponse
import com.example.bioSpecies.data.remote.services.AnimalService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.HashMap


class AnimalRepository(private val local: OperationDao, private val remote: Retrofit) {

    private val storage = ListStorage.getInstance()
    private var result = false

    //Animals

    fun getObservationSpecies() {
        val service = remote.create(AnimalService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getObservationSpecies(68229, "100")
            if (response.isSuccessful) {
                Log.i("ObservationSpecies", response.body().toString())
                val listaAnimal : ObservationSpeciesResponse? = response.body()
                var listaAnimalAves = mutableListOf<ObservationSpeciesResponse.Results>()
                var listaNomes = mutableListOf<String>()
                var listaAnimaisClasse = mutableListOf<AnimalClasse>()
                var listaAnimais = mutableListOf<Animal>()

                if (listaAnimal != null){ //verificar se a lista da response nao é null
                    for (a in listaAnimal.results){ //percorre a lista de animais
                        var result = true
                        var resultName = ""
                        for (nome in listaNomes){
                            if (nome == a.taxon.iconicTaxonName){
                                result = false
                            }
                        }
                        var listLocalizacoes = mutableListOf<Coordenadas>()
                        val animal = Animal(a.taxon.preferredCommonName, a.taxon.name, a.taxon.iconicTaxonName, a.taxon.id, a.taxon.defaultPhoto.mediumUrl, 0, 0 , listLocalizacoes, false) //cria animal a ser introduzido
                        listaAnimais.add(animal)
                        if (result){
                            listaNomes.add(a.taxon.iconicTaxonName) //adiciona a lista de nomes das classes
                            var listaAux = mutableListOf<Animal>() //cria lista auxiliar
                            listaAux.add(animal) // add do animal na lista auxiliar
                            val animalClasse = AnimalClasse(a.taxon.iconicTaxonName, listaAux) // cria  o AnimalClasse para adicionar a lista listaAnimais
                            listaAnimaisClasse.add(animalClasse) // adiciona
                        }else{
                            for (animalClasse in listaAnimaisClasse){ // percorre a lista das classes para saber em qual adicionar
                                if ( animalClasse.classe == a.taxon.iconicTaxonName){ //verifica qual e a classe para adicionar
                                    animalClasse.listAnimais.add(animal) // adiciona animal a lista da sua classe
                                }
                            }
                        }
                    }
                }

                storage.setListaAnimaisResult(listaAnimal!!.results)
                storage.updateListaAnimaisPorClasses(listaAnimaisClasse)
                storage.updateListaClasses(listaNomes)
                storage.updateListaAnimais(listaAnimais)
                storage.updateListaAnimaisFilter(listaAnimais)

                Log.i("listaAnimais", listaAnimal.results.size.toString())
                Log.i("listaNomes", listaNomes.toString())

                //getAnimalsplaces()

                getObservationAnimal()

            }else {
                Log.i("ObservationSpecies FAIL", response.errorBody().toString())
            }
        }
    }

    fun AnimalsConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            //getObservationSpecies() // vai buscar os animais a Api
            getObservacoesDataBase() // vai buscar a Base de Dados local
        }
    }

    //Places dos animais
    fun getAnimalsplaces() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = storage.getListAnimaisResult()
            var arrayId = mutableListOf<String>()

            for (i in list){
                arrayId.add(i.taxon.id.toString())
            }
            Log.i("ArrayID", arrayId.toString())

            //getPlacesSpecies(arrayId.toTypedArray())
        }
    }

    fun getPlacesSpecies(arrayId: Array<String>) {
        val service = remote.create(AnimalService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPlacesSpecies("144455")
            if (response.isSuccessful) {
                Log.i("PlacesSpecies", response.body().toString())
            }else {
                Log.i("PlacesSpecies FAIL", response.errorBody().toString())
            }
        }
    }

    //Observation de cada Especie, para obter a localizacao
    fun getObservationAnimal() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = storage.getListaAnimais()
            var result = 0
            var listAux = mutableListOf<String>()
            var listObservations = mutableListOf<ObservationLocations>()
            for (animal in 0..49) {
                getObservationAnimalPlaces(list[animal].taxonID.toString())
            }
//            for (animal in 50..99) {
//                getObservationAnimalPlaces(list[animal].taxonID.toString())
//            }
        }
    }


//    fun getObservationsApi(inicio : Int , fim : Int){
//        val duration = 2000L
//        val handle = Handler()
//        handle.postDelayed({
//            getObservationAnimal(inicio,fim)
//        }, duration)
//    }

    fun addObservationAnimalPlace(o: ObservationLocations) {
        storage.addObservationAnimalPlace(o)
    }

    fun getObservationAnimalPlaces(toTypedArray: String) {
        val service = remote.create(AnimalService::class.java)
        CoroutineScope(Dispatchers.IO).launch {

                val response = service.getObservationPlace(toTypedArray, 68229, true, "1", "10", "observed_on")
                if (response.isSuccessful) {
                    Log.i("ObservationAnimalPlaces", response.body().toString())
                    val listObservation : ObservationSpeciesPlaceResponse? = response.body()
                    var l = mutableListOf<Coordenadas>()
                    var observation : ObservationLocations = ObservationLocations(toTypedArray.toInt(), listObservation!!.totalResults, l)


                    for (observations in listObservation!!.results){
                        val r: List<String> = observations.location.split(",")
                        val coordenadas : Coordenadas = Coordenadas(r[0].toDouble(), r[1].toDouble())
                        observation.listCoordenadas.add(coordenadas)
                    }

                    Log.i("observationTaxon", observation.toString())
                    addObservationAnimalPlace(observation)

                    Log.i("verificaTamanho", storage.verificaTamanhoObservacionAndSpecies().toString())

                   verificaObservacoes()
                   //getObservacoesDataBase()

                }else {
                    Log.i("ObservationAnimal FAIL", response.errorBody().toString())
                    Log.i("ObservationAnimal FAIL", toTypedArray)

                }

        }
    }

    private fun getObservacoesDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = local.getAllAnimal()
            Log.i("getAnimalDatabase", list.toString())
            Log.i("getAnimalDatabase", list.size.toString())
            storage.updateListaAnimais(list.toMutableList())
            storage.updateListaAnimaisFilter(list.toMutableList())
            //atualizaAnimalDescoberto(list.toMutableList())
            //atualizaListAnimalClasses()
        }
    }

    private fun atualizaAnimalDescoberto(listAnimal: MutableList<Animal>) {
        val listPhotosEvaluated = storage.getListEvaluationPhotos()

        Log.i("PhotosE", listPhotosEvaluated.toString())

        for(photos in listPhotosEvaluated){
            for (animal in listAnimal){
                if (photos.animalName == animal.nomeTradicional){
                    animal.descoberto = true
                    Log.i("ADescoberto", animal.toString())
                    break
                }
            }
        }
        storage.updateListaAnimais(listAnimal)
        storage.updateListaAnimaisFilter(listAnimal)
    }


    fun atualizaListAnimalsFirebase(){
        atualizaAnimalDescoberto(storage.getListaAnimais())
        atualizaListAnimalClasses()
    }

    private fun atualizaListAnimalClasses(){
        val listaAnimal = storage.getListaAnimais()
        var listaNomes = mutableListOf<String>()
        var listaAnimaisClasse = mutableListOf<AnimalClasse>()

        if (listaAnimal != null){ //verificar se a lista da response nao é null
            for (a in listaAnimal){ //percorre a lista de animais
                var result = true
                var resultName = ""
                for (nome in listaNomes){
                    if (nome == a.classe){
                        result = false
                    }
                }
                val animal = Animal(a.nomeTradicional, a.nomeCientifico, a.classe , a.taxonID, a.fotoMediumUrl, a.totalObservationsResults, a.raridade , a.localizacao, a.descoberto) //cria animal a ser introduzido
                if (result){
                    listaNomes.add(a.classe) //adiciona a lista de nomes das classes
                    var listaAux = mutableListOf<Animal>() //cria lista auxiliar
                    listaAux.add(animal) // add do animal na lista auxiliar
                    val animalClasse = AnimalClasse(a.classe, listaAux) // cria  o AnimalClasse para adicionar a lista listaAnimais
                    listaAnimaisClasse.add(animalClasse) // adiciona
                }else{
                    for (animalClasse in listaAnimaisClasse){ // percorre a lista das classes para saber em qual adicionar
                        if ( animalClasse.classe == a.classe){ //verifica qual e a classe para adicionar
                            animalClasse.listAnimais.add(animal) // adiciona animal a lista da sua classe
                        }
                    }
                }
            }
        }

        storage.updateListaAnimaisPorClasses(listaAnimaisClasse)
        storage.updateListaClasses(listaNomes)

    }

    fun addAnimalDatabase(animal: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
            local.insertAnimal(animal)
        }
    }
    private fun verificaObservacoes() {
        if (storage.verificaTamanhoObservacionAndSpecies()){
            val listaObservacoes = storage.getObservationsList()
            var listaAnimais = storage.getListaAnimais()
            var mapObservacoes: HashMap<Int, ObservationLocations> = HashMap<Int, ObservationLocations> ()

            for (observation in listaObservacoes){
                mapObservacoes.put(observation.taxonId, observation)
            }

            var result = 0
            for (animal in listaAnimais){
                if (mapObservacoes.containsKey(animal.taxonID)) {
                val t: ObservationLocations? = mapObservacoes.get(animal.taxonID)
                Log.i("AnimalCoordenadas", t.toString())
                //storage.addNumberOfObservation(t!!.totalResults)

                    when {
                        t!!.totalResults < 200 -> {
                            animal.raridade = 3
                        }
                        t.totalResults in 200..399 -> {
                            animal.raridade = 2
                        }
                        t.totalResults in 400..799 -> {
                            animal.raridade = 1
                        }
                    }

                animal.localizacao = t!!.listCoordenadas
                animal.totalObservationsResults = t.totalResults
                addAnimalDatabase(animal)
                result++
                }
            }

            storage.verficaAllListTotalObservation()
            Log.i("listaAnimaisCompleta", listaAnimais.toString())
            Log.i("listaObservacoes", listaObservacoes.size.toString())
            storage.updateListaAnimais(listaAnimais)

        }
    }

    fun getCurrentWeek(): Int {
        val currentCalendar = Calendar.getInstance()
        val week = currentCalendar[Calendar.WEEK_OF_YEAR]
        return week
    }

    fun verificaSemana() {
        CoroutineScope(Dispatchers.IO).launch {
            val semana = local.getAllSemanas()
            if (semana.isEmpty()){
                val s : Semana = Semana(getCurrentWeek(), desafioComum = false, desafioPoucoRaro = false, desafioRaro = false, desafioMuitoRaro = false)
                local.insertSemana(s)
            }else{
                val s = semana[0]
                storage.setSemanaAnterior(s)
                for (i in 0..3){
                    when (i) {
                        0 -> {
                            storage.setChallengeConcluido(s.desafioComum,i)
                        }
                        1 -> {
                            storage.setChallengeConcluido(s.desafioPoucoRaro,i)
                        }
                        2 -> {
                            storage.setChallengeConcluido(s.desafioRaro,i)
                        }
                        3 -> {
                            storage.setChallengeConcluido(s.desafioMuitoRaro,i)
                        }
                    }
                }
            }

            if (semana.isNotEmpty()) {
                //verificar se e uma semana nova
                val s = semana[0]
                s.uuid = semana[0].uuid
                if (s.semanaAnterior != getCurrentWeek()) {
                    for (i in 0..3) {
                        when (i) {
                            0 -> {
                                s.desafioComum = false
                                storage.setChallengeConcluido(false, i)
                            }
                            1 -> {
                                s.desafioPoucoRaro = false
                                storage.setChallengeConcluido(false, i)
                            }
                            2 -> {
                                s.desafioRaro = false
                                storage.setChallengeConcluido(false, i)
                            }
                            3 -> {
                                s.desafioMuitoRaro = false
                                storage.setChallengeConcluido(false, i)
                            }
                        }
                    }
                    s.semanaAnterior = getCurrentWeek()
                    local.updateSemana(s)
                }

            }
        }
    }

    fun updateSemana(i: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val semana = local.getAllSemanas()
            val s = semana[0]
            s.uuid = semana[0].uuid
            when (i) {
                0 -> {
                    s.desafioComum = true
                }
                1 -> {
                    s.desafioPoucoRaro = true
                }
                2 -> {
                    s.desafioRaro = true
                }
                3 -> {
                    s.desafioMuitoRaro = true
                }
            }
            local.updateSemana(s)
        }
    }

    fun getEstatisticasDesafios() {
        CoroutineScope(Dispatchers.IO).launch {
            val estatisticasDesafios = local.getAllEstatisticasDesafios()

            if (estatisticasDesafios.isEmpty()){ //cria a variavel na dataBase se ainda nao existir
                val eD = EstatisticasDesafios(0,0,0)
                local.insertEstatisticasDesafios(eD)
                storage.updateEstatiscaDesafios(0,0,0)
            }else{ //atualiza com o que ta na dataBase
                val desafioFacil = estatisticasDesafios[0].desafioFacil
                val desafioMedio = estatisticasDesafios[0].desafioMedios
                val desafioDificil = estatisticasDesafios[0].desafioDificeis
                storage.updateEstatiscaDesafios(desafioFacil, desafioMedio, desafioDificil)

            }


        }
    }

    fun atualizaEstatisticas(i: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val estatisticasDesafios = local.getAllEstatisticasDesafios()
            val eD = estatisticasDesafios[0]
            eD.uuid = estatisticasDesafios[0].uuid

            when(i) {
                0 ->{
                    eD.desafioFacil++ //adiciona mais 1 na estatisticas faceis
                }
                1 ->{
                    eD.desafioFacil++ //adiciona mais 1 na estatisticas faceis
                }
                2 ->{
                    eD.desafioMedios++ //adiciona mais 1 na estatisticas medias
                }
                3 ->{
                    eD.desafioDificeis++ //adiciona mais 1 na estatisticas dificeis
                }
            }

            local.updateEstatisticasDesafios(eD) //atualiza na base de dados
                Log.i("updateEstatisticasD", eD.toString())

        }
    }

    fun addAninal(animal: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
           local.insertAnimal(animal)
        }
    }


}