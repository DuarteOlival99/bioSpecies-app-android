package com.example.bioSpecies.data.local.list

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.*
import com.example.bioSpecies.data.remote.responses.ObservationSpeciesResponse
import java.util.*

class ListStorage private constructor() {

    private var listCaderneta = mutableListOf<CadernetaItem>()
    private var listCaptureHistory = mutableListOf<CapturaItem>()
    private var listChallengesAtuais = mutableListOf<ChallengeItem>()
    private var groupSelect = ""
    private var location: Location? = null

    private var listaAnimaisTest = mutableListOf<Especies>()
    private var animalSelected : Animal? = null

    private var listUnevaluatedPhotos = mutableListOf<Photos>()
    private var listMyUnevaluatedPhotos = mutableListOf<Photos>()
    private var listMyPhotosEvatuated = mutableListOf<Photos>()
    private var listNoNamePhotos = mutableListOf<PhotosNoName>()
    private var listPhotosNoNameGame = mutableListOf<PhotosNoName>()


    private var listAnimaisResult = mutableListOf<ObservationSpeciesResponse.Results>()
    private var listaAnimaisClasse = mutableListOf<AnimalClasse>()
    private var listaAnimais = mutableListOf<Animal>()
    private var listaAnimaisFilter = mutableListOf<Animal>()
    private var listaNomesClasse = mutableListOf<String>()
    private var classClicked = ""

    private var filterCommon = true
    private var filterNotRare = true
    private var filterRare = true
    private var filterVeryRare = true
    private var filterDistance = 0.0

    private var listRankingNrCaptures = mutableListOf<RankingNrCapturas>()
    private var listRankingNrCapturesFinal = mutableListOf<RankingNrCapturasFinal>()

    private var myNrCapturas = "0"

    private lateinit var semanaAnterior : Semana
    private var estatisticasDesafios: EstatisticasDesafios = EstatisticasDesafios(0, 0, 0)

    private var listObservations = mutableListOf<ObservationLocations>()

    var listTotalObservasiont = mutableListOf<Int>()

    private var estatisticasCapturas = 0
    private var atualizaCapturas = false

    private var firstTime = false

    companion object {

        private var instance: ListStorage? = null

        fun getInstance(): ListStorage {
            synchronized(this) {
                if (instance == null) {
                    instance =
                        ListStorage()
                }
                return instance as ListStorage
            }
        }

    }
    fun getListTestAnimais() : List<Especies> {
        return listaAnimaisTest
    }

    fun animaisTest(){
        val esquiloVermelho = Especies("Esquilo-vermelho", "Sciurus vulgaris", criaListaLC("Porto de Mós", "Pedrogao Grande", "leiria"),
                criarListaCC(Coordenadas(39.574258, -8.810570), Coordenadas(39.749458, -8.808803)), "Rodentia", "Sciuridae",
                "Florestas de coníferas, florestas mistas, parques e jardins.", "Extinto em Portugal desde o século XVI, voltou a colonizar o norte do país a partir de populações espanholas. Foram tentadas algumas introduções no centro do país na década de 90, existindo actualmente uma população bem estabelecida no Parque Florestal de Monsanto e no Parque Natural da Serra da Estrela.",
                R.drawable.ic_esquilo_vermelho, 1)

        val leirao = Especies("Leirão", "Eliomys quercinus", criaListaLC("Porto de Mós", "Faro", "Mafra"),
                criarListaCC(Coordenadas(39.575319, -8.818898), Coordenadas(37.020044, -7.921724)), "Rodentia", "Gliridae",
                "Florestas de coníferas ou caducifólias, pomares e jardins.", "Também conhecido por leirão-dos-pomares ou rato-dos-pomares.",
                R.drawable.ic_leirao, 2)

        val lebre = Especies("Lebre", "Lepus granatensis", criaListaLC("Serro Ventoso", "Sintra", "Loures"),
                criarListaCC(Coordenadas(39.570539, -8.814660), Coordenadas(38.809919, -9.383930)), "Lagomorpha", "Leporidae",
                "Desde zonas agrícolas a bosques de montanha.", "Espécie cinegética.",
                R.drawable.ic_lebre, 0)

        listaAnimaisTest.add(esquiloVermelho)
        listaAnimaisTest.add(leirao)
        listaAnimaisTest.add(lebre)

    }

    fun criaListaLC(l1: String, l2: String, l3: String) : List<String> {
        val listaLocalizacoesComuns = mutableListOf<String>()
        listaLocalizacoesComuns.add(l1)
        listaLocalizacoesComuns.add(l2)
        listaLocalizacoesComuns.add(l3)
        return  listaLocalizacoesComuns
    }

    fun criarListaCC(c1: Coordenadas, c2: Coordenadas) : List<Coordenadas>{
        val listaLocalizacoesComuns = mutableListOf<Coordenadas>()
        listaLocalizacoesComuns.add(c1)
        listaLocalizacoesComuns.add(c2)
        return  listaLocalizacoesComuns
    }

    fun updateLocation(lastLocation: Location?) {
        location = lastLocation
    }

    fun criarListaTesteCaderneta() {
        val photo = R.drawable.ic__no_photography
        val nomeAnimal : String = " ????? "
        var coluna = 0
        var i = 0
        while (i < 20){
            if(listCaderneta.size == 0){
                coluna = 0
                i++
            }else if (i > 0 && listCaderneta[i - 1].colunaID == 3){
                coluna = 0
                i++
            }else{
                coluna++
                i++
            }
            val animal = CadernetaItem(nomeAnimal, photo, coluna);
            listCaderneta.add(animal)
            //Log.i("animal", "$i $animal")
        }
    }

    fun getListaCadernetaAnimais(): List<CadernetaItem> {
        criarListaTesteCaderneta()
        return listCaderneta
    }

    fun setGroupSelect(s: String) {
        groupSelect = s
    }

    fun criarListaTesteHistoricoCapturas(){
        val photo = R.drawable.ic__no_photography
        val nome = "teste"
        var classificacao = "aceite"

        var i = 0
        while (i < 10){
            if(i == 2 || i == 6){
                classificacao = "recusado"
                val captura = CapturaItem(photo, nome, classificacao);
                listCaptureHistory.add(captura)
            }else{
                classificacao = "aceite"
                val captura = CapturaItem(photo, nome, classificacao);
                listCaptureHistory.add(captura)
            }

            i++
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCaptureHistory(): List<Photos> {
        Log.i("listMyPhotosEvatuated", listMyPhotosEvatuated.toString())
        Log.i("listMyUnevaluatedPhotos", listMyUnevaluatedPhotos.toString())

        Collections.sort(listMyPhotosEvatuated)
        listMyPhotosEvatuated.reverse()

        return listMyUnevaluatedPhotos + listMyPhotosEvatuated
    }

    fun getChallengesAtuais(): List<ChallengeItem> {
        return listChallengesAtuais
    }

    private fun criarListaChallengesAtuais(comum: String, poucoRaro: String, raro: String, muitoRaro: String) {
        var concluido = false
        var descricao = ""
        val dificuldadeImagem = R.drawable.ic_ball
        var dificuldade = 0

        var i = 0
        while (i < 4){
            if(i == 0){
                descricao = comum
                dificuldade = 0
            }else if(i == 1){
                descricao = poucoRaro
                dificuldade = 0
            }else if(i == 2){
                descricao = raro
                dificuldade = 1
            }else{
                descricao = muitoRaro
                dificuldade = 2
            }

            val challenge = ChallengeItem(concluido, descricao, dificuldadeImagem, dificuldade)
            listChallengesAtuais.add(challenge)
            i++
        }
    }

    fun createListChallengesTest(comum: String, poucoRaro: String, raro: String, muitoRaro: String) {
        criarListaChallengesAtuais(comum, poucoRaro, raro, muitoRaro)
    }

    fun setListUnevaluatedPhotos(listImages: MutableList<Photos>) {
        listUnevaluatedPhotos = listImages
        Log.i("listUnevaluatedPhotos", listUnevaluatedPhotos.toString())
    }

    fun getListImagesUpload(): List<Photos> {
        return listUnevaluatedPhotos
    }

    fun setListEvaluatedPhotos(listImages: MutableList<Photos>) {
        listMyPhotosEvatuated = listImages
        Log.i("setListEvaluatedPhotos", listMyPhotosEvatuated.toString())
    }

    fun getListEvaluationPhotos() : List<Photos>{
        Log.i("listMyPhotosEvatuated", listMyPhotosEvatuated.toString())
        return listMyPhotosEvatuated
    }

    fun setAnimalSelected(animal: Animal) {
        animalSelected = animal
    }

    fun getAnimalSelected(): Animal? {
        return animalSelected
    }

    fun setListMyUnevaluatedPhotos(listImages: MutableList<Photos>) {
        listMyUnevaluatedPhotos = listImages
        Log.i("myUnevaluatedPhotos", listMyUnevaluatedPhotos.toString())
    }

    fun addMoreOneCapture() {
        estatisticasCapturas++
    }

    fun getCaptureEstatics(): Int {
        return listMyUnevaluatedPhotos.size + listMyPhotosEvatuated.size
    }

    fun atualizaCaptureEstatisticas() {
//        if (!atualizaCapturas){
//            var totalFotosDatabase = 0
//            totalFotosDatabase += listMyUnevaluatedPhotos.size + listMyPhotosEvatuated.size
//            estatisticasCapturas += totalFotosDatabase
//            atualizaCapturas = true
//        }
    }

    fun updateListaAnimaisPorClasses(listaAnimaisClasse: List<AnimalClasse>){
        this.listaAnimaisClasse = listaAnimaisClasse as MutableList<AnimalClasse>
    }

    fun updateListaClasses(listaNomes: MutableList<String>) {
        this.listaNomesClasse = listaNomes
    }

    fun getListaNomesClasses(): List<String> {
        return listaNomesClasse
    }

    fun setClassClicked(s: String) {
        classClicked = s
    }

    fun getListaAnimaisClasse(): MutableList<AnimalClasse> {
        return listaAnimaisClasse
    }

    fun getClasseSelected(): String {
        return classClicked
    }

    fun setAnimalSelectedCaderneta(animal: Animal) {
        animalSelected = animal
    }

    fun setListaAnimaisResult(results: MutableList<ObservationSpeciesResponse.Results>) {
        listAnimaisResult = results
    }

    fun getListAnimaisResult(): MutableList<ObservationSpeciesResponse.Results> {
        return listAnimaisResult
    }

    fun addObservationAnimalPlace(o: ObservationLocations) {
        listObservations.add(o)
    }

    fun verificaTamanhoObservacionAndSpecies(): Boolean {
        Log.i("verificaTamanho", listObservations.size.toString())
        return listObservations.size == 50 //listAnimaisResult.size
    }

    fun getObservationsList(): List<ObservationLocations> {
        return listObservations
    }

    fun updateListaAnimais(listaAnimais: MutableList<Animal>) {
        this.listaAnimais = listaAnimais
    }

    fun getListaAnimais(): MutableList<Animal> {
        return listaAnimais
    }

    fun addNumberOfObservation(totalResults: Int) {
        listTotalObservasiont.add(totalResults)
    }

    fun verficaAllListTotalObservation(){
        listTotalObservasiont.sort()
        Log.i("listTotalObservasiont", listTotalObservasiont.toString())
    }

    fun updateListaAnimaisFilter(listaAnimaisFilter: MutableList<Animal>) {
        this.listaAnimaisFilter = listaAnimaisFilter
    }

    fun getListAnimaisFilter(): List<Animal> {
        return listaAnimaisFilter
    }

    fun getFilterCommon(): Boolean {
        return filterCommon
    }

    fun getFilterNotRare(): Boolean {
        return filterNotRare
    }

    fun getFilterRare(): Boolean {
        return filterRare
    }

    fun getFilterVeryRare(): Boolean {
        return filterVeryRare
    }

    fun setFilterCommon(checked: Boolean) {
        this.filterCommon = checked
    }

    fun setFilterNotRare(checked: Boolean) {
        this.filterNotRare = checked
    }

    fun setFilterRare(checked: Boolean) {
        this.filterRare = checked
    }

    fun setFilterVeryRare(checked: Boolean) {
        this.filterVeryRare = checked
    }

    fun getFIlterDistance(): Double {
        return filterDistance
    }

    fun getLocation(): Location? {
        return location
    }

    fun setFilterDistance(distance: Double) {
        this.filterDistance = distance
    }

    fun addUnevaluatedPhoto(imageUnevaluated: Photos) {
        listMyUnevaluatedPhotos.add(imageUnevaluated)
    }

    fun setDesafioConcluido(i: Int) {
        listChallengesAtuais[i].concluido = true
    }

    fun setSemanaAnterior(semanaAnterior: Semana) {
        this.semanaAnterior = semanaAnterior
    }

    fun setChallengeConcluido(desafio: Boolean, i: Int) {
        listChallengesAtuais[i].concluido = desafio
    }

    fun updateEstatiscaDesafios(dF: Int, dM: Int, dD: Int) {
        this.estatisticasDesafios.desafioFacil = dF
        this.estatisticasDesafios.desafioMedios = dM
        this.estatisticasDesafios.desafioDificeis = dD
    }

    fun atualizaEstatisticas(i: Int) {
        when(i) {
            0 -> {
                estatisticasDesafios.desafioFacil++ //adiciona mais 1 na estatisticas faceis
            }
            1 -> {
                estatisticasDesafios.desafioFacil++ //adiciona mais 1 na estatisticas faceis
            }
            2 -> {
                estatisticasDesafios.desafioMedios++ //adiciona mais 1 na estatisticas medias
            }
            3 -> {
                estatisticasDesafios.desafioDificeis++ //adiciona mais 1 na estatisticas dificeis
            }
        }
    }

    fun getEstatisticasDesafios() : EstatisticasDesafios{
        return estatisticasDesafios
    }

    fun addAnimal(animal: Animal) {
        Log.i("SAnimal", animal.toString())
        listaAnimais.add(animal)
        listaAnimaisFilter.add(animal)
    }

    fun validateFirstTimeFirebase(): Boolean {
        return firstTime
    }

    fun setListNoNamePhotos(listImages: MutableList<PhotosNoName>) {
        this.listNoNamePhotos = listImages
    }

    fun getListPhotosNoName(): List<PhotosNoName>  {
        return listNoNamePhotos
    }

    fun setListNoNameGamePhotos(listImages: MutableList<PhotosNoName>) {
        this.listPhotosNoNameGame = listImages
    }

    fun getListNoNameGamePhotos() : List<PhotosNoName> {
        return listPhotosNoNameGame
    }

    fun removePhotoEvaluate(pos: Photos) {
        listUnevaluatedPhotos.remove(pos)
    }

    fun atualizalistRankingNrCapturas(listRanking: MutableList<RankingNrCapturasFinal>) {
        this.listRankingNrCapturesFinal = listRanking
    }

    fun getlistRankingNrCapturas() : List<RankingNrCapturasFinal> {
        return listRankingNrCapturesFinal
    }

    fun atualizaMyNrcapturas(myNrCapturas: String) {
        this.myNrCapturas = myNrCapturas
    }

    fun getMyNrCapturas() : String {
        return myNrCapturas
    }

    fun addRankingNrCapturas(user: RankingNrCapturasFinal) {
        listRankingNrCapturesFinal.add(user)
    }

    fun removePhotoEvaluateNoName(photosNoName: PhotosNoName) {
        listNoNamePhotos.remove(photosNoName)
    }

    fun removePhotoEvaluateNoNameGame(photosNoName: PhotosNoName) {
        listPhotosNoNameGame.remove(photosNoName)
    }

    fun clearListChallenges() {
        listChallengesAtuais.clear()
    }


}