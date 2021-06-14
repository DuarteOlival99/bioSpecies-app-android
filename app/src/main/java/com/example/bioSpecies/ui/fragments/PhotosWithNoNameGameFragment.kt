package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.FotoNomeAtribuido
import com.example.bioSpecies.data.entity.FotoNomeAtribuidoGame
import com.example.bioSpecies.data.entity.PhotoEvaluation
import com.example.bioSpecies.data.entity.PhotosNoName
import com.example.bioSpecies.ui.viewmodels.viewmodels.CameraViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.camera_fragment.*
import kotlinx.android.synthetic.main.evaluation_fragment.*
import kotlinx.android.synthetic.main.photos_game_fragment.*
import kotlinx.android.synthetic.main.photos_with_no_name_fragment.*
import java.io.ByteArrayOutputStream


class PhotosWithNoNameGameFragment : Fragment(){

    private lateinit var viewModel: CameraViewModel
    private var imageView: ImageView? = null

    var mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var mFirebaseStorage = FirebaseStorage.getInstance()
    private var mDatabaseStorage = FirebaseDatabase.getInstance()

    // create a reference to storage
    private val storageRef = mFirebaseStorage.reference

    private var listUploadsNoNameGame = listOf<PhotosNoName>()

    var pos = 0

    var currentUser : FirebaseUser? = null
    var mDatabaseRef: DatabaseReference? = null
    val nivelMaximo = 999


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.photos_game_fragment, container, false)
        imageView = view!!.findViewById<View>(R.id.foto_sem_nome_game) as ImageView
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onStart() {
        super.onStart()

        getImages()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference
    }

    fun getImages(){
        var userID: String = FirebaseAuth.getInstance().currentUser!!.uid
        listUploadsNoNameGame = viewModel.getListPhotosNoNameGame()
        Log.i("listUploadsNoNameGame", listUploadsNoNameGame.toString())
        if (listUploadsNoNameGame.isNotEmpty()){
            setImages()
        } else  {
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    fun setImages(){
        Log.i("setImages", "setImages")
        foto_sem_nome_game.setImageBitmap(listUploadsNoNameGame[pos].image)

        //meter respostas nos botoes
        introduzirNomesButoesRespostas()
    }

    fun introduzirNomesButoesRespostas(){
        var listRespostas = mutableListOf<String>()
        for (i in 1 until listUploadsNoNameGame[pos].listAnimalName.size){
            if (!listRespostas.contains(listUploadsNoNameGame[pos].listAnimalName[i].nomeAtribuido)){
                listRespostas.add(listUploadsNoNameGame[pos].listAnimalName[i].nomeAtribuido)
            }
        }

        when (listRespostas.size){
            2 -> {
                button_send_answer3_game.visibility = View.GONE
                button_send_answer4_game.visibility = View.GONE

                button_send_answer1_game.text = listRespostas[0]
                button_send_answer2_game.text = listRespostas[1]
            }
            3 -> {
                button_send_answer4_game.visibility = View.GONE

                button_send_answer1_game.text = listRespostas[0]
                button_send_answer2_game.text = listRespostas[1]
                button_send_answer3_game.text = listRespostas[2]
            }
            4 -> {
                button_send_answer1_game.text = listRespostas[0]
                button_send_answer2_game.text = listRespostas[1]
                button_send_answer3_game.text = listRespostas[2]
                button_send_answer4_game.text = listRespostas[3]
            }

        }
    }

    fun naoTemMaisAvaliacoes(){
        atribuir_nome_game.text = getString(R.string.no_name_photo_game)
        foto_sem_nome_game.setImageResource(R.drawable.ic__no_photography)
        constraint_butoes_game.visibility = View.GONE
    }

    private fun photoGameAnswered(answer: String) {
        var listNames = mutableListOf<FotoNomeAtribuidoGame>()

        val lastName = answer
        val lastUser = mAuth?.uid.toString()
        val fotoName = FotoNomeAtribuidoGame(lastName, lastUser)

        var animalNameAnswer = ""
        var animalAnswerUser = ""

        for (name in listUploadsNoNameGame[pos].listAnimalNameGame) {
            if (name.nomeAtribuido != "" || name.nomeAtribuidoUser != ""){
                listNames.add(name)

                animalNameAnswer += name.nomeAtribuido
                animalNameAnswer += ";"

                animalAnswerUser += name.nomeAtribuidoUser
                animalAnswerUser += ";"
            }
        }

        listNames.add(fotoName)
        animalNameAnswer += lastName
        animalAnswerUser += lastUser

        updateDataAnimalsGame(listUploadsNoNameGame[pos], listNames)

        var nomeMaisVotado = ""

        Log.i("listUploadsNoNameGameS", listUploadsNoNameGame[pos].listAnimalNameGame.size.toString())

        if (listUploadsNoNameGame[pos].listAnimalNameGame.size % 5 == 0){
            Log.i("updateMetadate", "1")
            Log.i("listAnimalName", listUploadsNoNameGame[pos].listAnimalNameGame.toString())

            var listNomes = mutableListOf<String>()
            var listValor = mutableListOf<Int>()

            for (i in listUploadsNoNameGame[pos].listAnimalNameGame.indices){
                if (!listNomes.contains( listUploadsNoNameGame[pos].listAnimalNameGame[i].nomeAtribuido)){
                    listNomes.add(listUploadsNoNameGame[pos].listAnimalNameGame[i].nomeAtribuido)
                    var count = 0
                    for (element in listUploadsNoNameGame[pos].listAnimalNameGame){
                        if (element.nomeAtribuido == listUploadsNoNameGame[pos].listAnimalNameGame[i].nomeAtribuido){
                            count++
                        }
                    }
                    listValor.add(count)
                }
            }

            var maiorValorPos = 0
            for (pos in 0 until listValor.size){
                if (listValor[pos] > listValor[maiorValorPos]){
                    maiorValorPos = pos
                }
            }

            nomeMaisVotado = listNomes[maiorValorPos]

            var resultValor = false
            var value = listValor[maiorValorPos]
            for (v in 0 until listValor.size){
                if (listValor[v] == value && v != maiorValorPos ){
                    resultValor = true
                }
            }

            if (resultValor){
                updateMetadate(listUploadsNoNameGame[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado, 0)
            }else{
                updateMetadate(listUploadsNoNameGame[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado, 1)
                adicionarUserXPEscolha(listUploadsNoNameGame[pos], nomeMaisVotado)
            }
        }else{
            Log.i("updateMetadate", "0")
            updateMetadate(listUploadsNoNameGame[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado, 0)
        }

    }

    private fun adicionarUserXPEscolha(photo: PhotosNoName, nomeMaisVotado: String) {

        // 10 Xp -> acertou so no Game
        // 20 XP -> acertou no palpite do nome
        // 30 XP -> acertou no game e palpite

        var listAllUsers = mutableListOf<String>()

        for (user in photo.listAnimalNameGame){
            if (!listAllUsers.contains(user.nomeAtribuidoUser) && user.nomeAtribuidoUser != ""){
                listAllUsers.add(user.nomeAtribuidoUser)
            }
        }

        for (user in photo.listAnimalName){
            if (!listAllUsers.contains(user.nomeAtribuidoUser) && user.nomeAtribuidoUser != ""){
                listAllUsers.add(user.nomeAtribuidoUser)
            }
        }

        for (user in listAllUsers){

            var adicionarXP = 0

            for (p in photo.listAnimalNameGame){
                if (p.nomeAtribuidoUser == user){
                    if (p.nomeAtribuido == nomeMaisVotado){
                        adicionarXP += 10
                        break
                    }
                }
            }

            for (p in photo.listAnimalName){
                if (p.nomeAtribuidoUser == user){
                    if (p.nomeAtribuido == nomeMaisVotado){
                        adicionarXP += 20
                        break
                    }
                }
            }

            if (adicionarXP > 0){
                adicionarUserXP(user, adicionarXP)
            }

        }

    }

    private fun adicionarUserXP(user : String, adicionarXP: Int) { // adicionar Xp ao user na firebase

        var nivelAtual: Int = 0
        var nivelSeguinte: Int = 0
        var xpAtual: Int = 0
        var xpMax: Int = 0

        Log.i("adicionarXP", "entrou ${user}")
        // Read from the database
        mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called ONLY ONCE (addListenerForSingleValueEvent) with the initial value
                val userID: String = user
                val childUser = dataSnapshot.child("Users").child(userID)

                //get dos valores da BD
                nivelAtual = childUser.child("Nivel").child("nivel_atual").value.toString().toInt()
                nivelSeguinte = nivelAtual + 1
                xpAtual = childUser.child("Nivel").child("xp_atual").value.toString().toInt()
                xpMax = childUser.child("Nivel").child("xp_max").value.toString().toInt()
                Log.i("CF->adicionarUserXP", "Dados lidos da BD: nivelAtual = $nivelAtual   nivelSeguinte = $nivelSeguinte   xpAtual = $xpAtual   xpMax = $xpMax")

                //adicionar xp e ver se passou de nivel
                var userRef = mDatabaseRef?.child("Users")?.child(user)

                if ( adicionarXP == 20 || adicionarXP == 30){
                    //atualiza dados estatistica nomes atribuidos
                    var estatiticaNrNomesAtribuido = childUser.child("Estatisticas").child("nr_avaliacoes_nome_atribuido").value.toString().toInt()
                    estatiticaNrNomesAtribuido++
                    userRef?.child("Estatisticas")?.child("nr_avaliacoes_nome_atribuido")?.setValue(estatiticaNrNomesAtribuido)
                }

                if ( adicionarXP == 10 || adicionarXP == 30){
                    //atualiza dados estatistica nomes atribuidos Jogo
                    var estatiticaNrNomesAtribuidoJogo = childUser.child("Estatisticas").child("nr_avaliacoes_jogo").value.toString().toInt()
                    estatiticaNrNomesAtribuidoJogo++
                    userRef?.child("Estatisticas")?.child("nr_avaliacoes_jogo")?.setValue(estatiticaNrNomesAtribuidoJogo)
                }

                var resultado = xpAtual + adicionarXP
                if(resultado >= xpMax) {  //se passou de nivel
                    //aritmetica para dar os valores corretos para cada nivel dependendo do valor de xp a ser adicionado
                    // nivel 1 -> 100xp, nivel 2 -> 200xp, nivel 3 -> 300xp, etc
                    var novoNivelAtual = 0
                    var novoXpAtual = xpAtual
                    var novoXpMax = novoNivelAtual * 100

                    for ( i in nivelAtual..nivelMaximo ) {
                        novoNivelAtual = i
                        novoXpMax = i * 100
                        if (resultado > novoXpMax) { //passou de nivel
                            //
                        } else if (resultado == novoXpMax) { //passou de nivel e fica r = 0
                            novoNivelAtual++
                            novoXpAtual = 0
                            break
                        } else { //r < xpMax -> nao passou de nivel
                            novoXpAtual = resultado
                            break
                        }
                        resultado -= novoXpMax
                    }

                    novoXpMax = novoNivelAtual * 100

                    //set dos valores (nivelAtual, xpAtual, xpMax) na RealTime BD
                    userRef?.child("Nivel")?.child("nivel_atual")?.setValue(novoNivelAtual)
                    userRef?.child("Nivel")?.child("xp_atual")?.setValue(novoXpAtual)
                    userRef?.child("Nivel")?.child("xp_max")?.setValue(novoXpMax)
                }else{ //se continuar no mesmo nivel
                    //set dos valores (xpAtual) na RealTime BD
                    userRef?.child("Nivel")?.child("xp_atual")?.setValue(resultado)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Do nothing (é preciso criar a função por causa do handler, mas não é usada)
            }
        })

    }

    fun updateMetadate(animalTitle: String, animalUuidUser: String, animalNameAnswer: String, nomeMaisVotado: String, resultado: Int){
        val photoRef = storageRef.child("images/camera/photosWithNoNameGame/$animalTitle")
        val metadata = storageMetadata {
            setCustomMetadata("nomesAtribuidosGame", animalNameAnswer)
            setCustomMetadata("userNomesAtribuidosGame", animalUuidUser)
            setCustomMetadata("nomeMaisVotado", nomeMaisVotado)
        }

        photoRef.updateMetadata(metadata).addOnSuccessListener {
            // Updated metadata is in storageMetadata
            val resultado =  it.getCustomMetadata("resultado")
        }.addOnFailureListener {
        }

        when (resultado) {
            1 -> {
                addNewLocationPhotoEvaluated(listUploadsNoNameGame[pos], animalUuidUser, animalNameAnswer, nomeMaisVotado)
            }
            else -> {
                Log.i("nextPhoto", "nextPhoto")
                nextPhoto()
            }
        }


    }

    fun nextPhoto(){
        if (listUploadsNoNameGame.isNotEmpty()){
            Log.i("nextPhoto", "nextPhoto entrou")
            if (listUploadsNoNameGame.size == 1){
                Toast.makeText(context, "Resposta guardade, proxima foto apresentada", Toast.LENGTH_LONG).show()
                viewModel.removePhotoEvaluate(listUploadsNoNameGame[pos])
                naoTemMaisAvaliacoes()
            }else{
                viewModel.removePhotoEvaluate(listUploadsNoNameGame[pos])
                listUploadsNoNameGame = viewModel.getListPhotosNoName()
                setImages()
            }
        } else  {
            viewModel.removePhotoEvaluateNoNameGame(listUploadsNoNameGame[pos])
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    fun addNewLocationPhotoEvaluated(animal: PhotosNoName, animalUuidUser: String, animalNameAnswer: String, nomeMaisVotado: String){
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        animal.image?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val data = outputStream.toByteArray()

        var inputNameAnimal = nomeMaisVotado + animal.title

        val path: String = "images/camera/evaluatedPhotos/"  + inputNameAnimal
        val mStorageRef: StorageReference = mFirebaseStorage.getReference(path)
        val mDatabaseRef: DatabaseReference =
                mDatabaseStorage.getReference("image/camera/evaluatedPhotos/" + animal.userID + "/")

        var nomesAtribuidos = ""
        var nomesAtribuidosUsers = ""

        for (atribuicao in animal.listAnimalName){
            nomesAtribuidos += atribuicao.nomeAtribuido
            nomesAtribuidosUsers += atribuicao.nomeAtribuidoUser
        }


        val metadata: StorageMetadata = StorageMetadata.Builder()
                .setCustomMetadata("user", animal.userID)
                .setCustomMetadata("resultado", "1")
                .setCustomMetadata("avaliada?", "true")
                .setCustomMetadata("userJaViu?",  animal.userJaViu.toString())
                .setCustomMetadata("nomesAtribuidos", nomesAtribuidos)
                .setCustomMetadata("userNomesAtribuidos", nomesAtribuidosUsers)
                .setCustomMetadata("nomeMaisVotado", nomeMaisVotado)
                .setCustomMetadata("nomesAtribuidosGame",animalNameAnswer)
                .setCustomMetadata("userNomesAtribuidosGame", animalUuidUser)
                .build()

        val uploadTask: UploadTask = mStorageRef.putBytes(data, metadata)

        uploadTask.addOnFailureListener(context as Activity) { e ->
            Toast.makeText(
                    context, "Precisa de Internet para fazer as avaliacoes: " +
                    e.message, Toast.LENGTH_LONG
            ).show()
        }.addOnSuccessListener(context as Activity,
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot -> //Uri url = taskSnapshot.getDownloadUrl();
                    val uri: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uri.isComplete);
                    val url: Uri = uri.result!!
                    //mDatabaseRef.push().setValue(upload)

                    removePhotoLocation(animal)

                    nextPhoto()

                    Log.i("FBApp1_URL ", url.toString())
                })
    }

    fun removePhotoLocation(animal : PhotosNoName){
        val photoRef = storageRef.child("images/camera/photosWithNoNameGame/${animal.title}")
        // delete the file
        mFirebaseStorage.reference.child("images/camera/photosWithNoNameGame/${animal.title}").delete()
    }

    private fun updateDataAnimalsGame(photosNoName: PhotosNoName, listNames: MutableList<FotoNomeAtribuidoGame>) {
        photosNoName.listAnimalNameGame = listNames
    }

    @OnClick(R.id.button_send_answer1_game)
    fun onClickButtonSendAnswer1Game(view: View){
        photoGameAnswered(button_send_answer1_game.text.toString())
    }

    @OnClick(R.id.button_send_answer2_game)
    fun onClickButtonSendAnswer2Game(view: View){
        photoGameAnswered(button_send_answer2_game.text.toString())
    }

    @OnClick(R.id.button_send_answer3_game)
    fun onClickButtonSendAnswer3Game(view: View){
        photoGameAnswered(button_send_answer3_game.text.toString())
    }

    @OnClick(R.id.button_send_answer4_game)
    fun onClickButtonSendAnswer4Game(view: View){
        photoGameAnswered(button_send_answer4_game.text.toString())
    }

}