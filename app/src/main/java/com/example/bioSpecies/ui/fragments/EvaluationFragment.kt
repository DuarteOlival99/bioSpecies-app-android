package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.PhotoEvaluation
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.viewmodels.EvaluationViewModel
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
import kotlinx.android.synthetic.main.evaluation_fragment.*
import java.io.ByteArrayOutputStream


class EvaluationFragment : Fragment(){

    private lateinit var viewModel : EvaluationViewModel

    private var mFirebaseStorage = FirebaseStorage.getInstance()
    private var mDatabaseStorage = FirebaseDatabase.getInstance()

    val nivelMaximo = 999

    var mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var currentUser : FirebaseUser? = null
    var mDatabaseRef: DatabaseReference? = null

    // create a reference to storage
    private val storageRef = mFirebaseStorage.reference

    private var listUploads = listOf<Photos>()

    var pos = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.evaluation_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(EvaluationViewModel::class.java)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference
    }

    override fun onStart() {
        super.onStart()
        getImages()
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }

    fun getImages(){
        var userID: String = FirebaseAuth.getInstance().currentUser!!.uid
        listUploads = viewModel.getListUpload(userID)
        if (listUploads.isNotEmpty()){
            setImagesAndName()
        } else  {
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    fun setImagesAndName(){
        avaliacao_foto_user.setImageBitmap(listUploads[pos].image)
        avaliacao_fotos_nome_especie_text.text = listUploads[pos].animalName
    }

    fun updateDataAnimalsList(animal: Photos, listNames: MutableList<PhotoEvaluation>){
        animal.listPhotoEvaluation = listNames
    }

    fun updateDataAnimals(animal: Photos, resultado: Int){
        //calcula se e true ou false para ver o que ganha
            if ( resultado == 1) {
                animal.resultado = resultado
                animal.avaliada = true
            }else{
                animal.resultado = resultado
                animal.avaliada = false

            }

    }

    fun updateMetadate(animalTitle: String, animalNameAnswerUser: String, animalNameAnswer: String, nomeMaisVotado: String, animalResultado : Int){
        val photoRef = storageRef.child("images/camera/unevaluatedPhotos/$animalTitle")
        val metadata = storageMetadata {
            setCustomMetadata("resultado", animalResultado.toString())
            if (animalResultado == 1 || animalResultado == 0){
                setCustomMetadata("avaliada?", true.toString())
            }else{
                setCustomMetadata("avaliada?", false.toString())
            }
            setCustomMetadata("photosEvaluation", animalNameAnswer)
            setCustomMetadata("photosEvaluationUser", animalNameAnswerUser)
        }

        photoRef.updateMetadata(metadata).addOnSuccessListener {
            // Updated metadata is in storageMetadata
            val resultado =  it.getCustomMetadata("resultado")
        }.addOnFailureListener {
        }

        when (animalResultado) {
            1 -> {
                Log.i("nextPhoto", "0")
                addNewLocationPhoto(listUploads[pos], animalNameAnswerUser, animalNameAnswer)
            }
            0 -> {
                Log.i("nextPhoto", "1")
                addNewLocationPhoto(listUploads[pos], animalNameAnswerUser, animalNameAnswer)
            }
            else -> {
                Log.i("nextPhoto", "updateMetadateNextPhoto")
                nextPhoto()
            }
        }

    }

    fun addNewLocationPhoto(animal : Photos, animalNameAnswerUser: String, animalNameAnswer: String){
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        animal.image?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val data = outputStream.toByteArray()

        var inputNameAnimal = animal.title

        val path: String = "images/camera/evaluatedPhotos/"  + inputNameAnimal
        val mStorageRef: StorageReference = mFirebaseStorage.getReference(path)
        val mDatabaseRef: DatabaseReference =
                mDatabaseStorage.getReference("image/camera/evaluatedPhotos/" + animal.userID + "/")

        val metadata: StorageMetadata = StorageMetadata.Builder()
                .setCustomMetadata("user", animal.userID)
                .setCustomMetadata("avaliada?", animal.avaliada.toString())
                .setCustomMetadata("resultado",  animal.resultado.toString())
                .setCustomMetadata("userJaViu?",  animal.userJaViu.toString())
                .setCustomMetadata("photosEvaluation", animalNameAnswer)
                .setCustomMetadata("photosEvaluationUser", animalNameAnswerUser)

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
//                    val upload = Upload(
//                            path,
//                            url.toString(),
//                            foto_name_input.text.toString()
//                    )
                    //mDatabaseRef.push().setValue(upload)
                    Toast.makeText(
                            context, "Avaliacao guardada", Toast.LENGTH_SHORT
                    ).show()
                    removePhotoLocation(animal)
                    nextPhoto()
                    Log.i("FBApp1_URL ", url.toString())
                })
    }

    fun nextPhoto(){
        if (pos < listUploads.size-1){
            Toast.makeText(context, "Resposta guardade, proxima foto apresentada", Toast.LENGTH_LONG).show()
            Log.i("nextPhoto", "nextPhoto entrou")
            viewModel.removePhotoEvaluate(listUploads[pos])
            pos++
            setImagesAndName()
        } else  {
            viewModel.removePhotoEvaluate(listUploads[pos])
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    fun removePhotoLocation(animal : Photos){
        val photoRef = storageRef.child("images/camera/unevaluatedPhotos/${animal.title}")
        // delete the file
        mFirebaseStorage.reference.child("images/camera/unevaluatedPhotos/${animal.title}").delete()
    }

    fun naoTemMaisAvaliacoes(){
        avaliacao_fotos.text = getString(R.string.nao_tem_mais_fotos)
        avaliacao_foto_user.setImageResource(R.drawable.ic__no_photography)
        constraint_nome_especie.visibility = View.GONE
        constraint_buttoes.visibility = View.GONE
    }

    private fun adicionarUserXP(photoEvaluation: List<PhotoEvaluation>, maisVotado: String, adicionarXP: Int) { // adicionar Xp ao user na firebase

        var nivelAtual: Int = 0
        var nivelSeguinte: Int = 0
        var xpAtual: Int = 0
        var xpMax: Int = 0
        var estatiticaNrAvaliacoes = 0

        for (evaluation in photoEvaluation){
            if (evaluation.evaluation == maisVotado){
                Log.i("adicionarXP", "entrou ${evaluation.evaluationUser}")
                // Read from the database
                mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called ONLY ONCE (addListenerForSingleValueEvent) with the initial value
                        val userID: String = evaluation.evaluationUser
                        val childUser = dataSnapshot.child("Users").child(userID)

                        //get dos valores da BD
                        nivelAtual = childUser.child("Nivel").child("nivel_atual").value.toString().toInt()
                        nivelSeguinte = nivelAtual + 1
                        xpAtual = childUser.child("Nivel").child("xp_atual").value.toString().toInt()
                        xpMax = childUser.child("Nivel").child("xp_max").value.toString().toInt()
                        Log.i("CF->adicionarUserXP", "Dados lidos da BD: nivelAtual = $nivelAtual   nivelSeguinte = $nivelSeguinte   xpAtual = $xpAtual   xpMax = $xpMax")

                        //adicionar xp e ver se passou de nivel
                        var userRef = mDatabaseRef?.child("Users")?.child(evaluation.evaluationUser)

                        //atualiza dados estatistica
                        estatiticaNrAvaliacoes = childUser.child("Estatisticas").child("nr_avaliacoes").value.toString().toInt()
                        estatiticaNrAvaliacoes++
                        userRef?.child("Estatisticas")?.child("nr_avaliacoes")?.setValue(estatiticaNrAvaliacoes)


                        //atualiza nivel XP
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
        }

    }

    private fun evaluationClick(answer: String) {
        var listNames = mutableListOf<PhotoEvaluation>()

        val lastName = answer
        val lastUser = mAuth?.uid.toString()
        val fotoName = PhotoEvaluation(lastName, lastUser)

        var animalNameAnswer = ""
        var animalAnswerUser = ""

        for (name in listUploads[pos].listPhotoEvaluation) {
            if (name.evaluation != "" || name.evaluationUser != ""){
                listNames.add(name)

                animalNameAnswer += name.evaluation
                animalNameAnswer += ";"

                animalAnswerUser += name.evaluationUser
                animalAnswerUser += ";"
            }
        }

        listNames.add(fotoName)
        animalNameAnswer += lastName
        animalAnswerUser += lastUser

        updateDataAnimalsList(listUploads[pos], listNames)

        var nomeMaisVotado = ""

        if (listUploads[pos].listPhotoEvaluation.size % 5 == 0){
            Log.i("updateMetadate", "1")
            Log.i("listAnimalName", listUploads[pos].listPhotoEvaluation.toString())

            var listNomes = mutableListOf<String>()
            var listValor = mutableListOf<Int>()

            for (i in listUploads[pos].listPhotoEvaluation.indices){
                if (!listNomes.contains( listUploads[pos].listPhotoEvaluation[i].evaluation)){
                    listNomes.add(listUploads[pos].listPhotoEvaluation[i].evaluation)
                    var count = 0
                    for (element in listUploads[pos].listPhotoEvaluation){
                        if (element.evaluation == listUploads[pos].listPhotoEvaluation[i].evaluation){
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

            Log.i("resultValor", resultValor.toString())
            Log.i("nomeMaisVotado", nomeMaisVotado.toString())

            if (resultValor){
                updateMetadate(listUploads[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado,  -1)
            }else{
                if (nomeMaisVotado == true.toString()){
                    updateDataAnimals(listUploads[pos], 1)
                    updateMetadate(listUploads[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado,  1)
                    adicionarUserXP(listUploads[pos].listPhotoEvaluation, "true", 5)
                }else if (nomeMaisVotado == false.toString()){
                    updateDataAnimals(listUploads[pos], 0)
                    updateMetadate(listUploads[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado,  0)
                    adicionarUserXP(listUploads[pos].listPhotoEvaluation, "false", 5)
                }
            }
        }else{
            Log.i("updateMetadate", "-1")
            updateMetadate(listUploads[pos].title, animalAnswerUser, animalNameAnswer, nomeMaisVotado, -1)
        }
    }

    @OnClick(R.id.button_reject)
    fun onClickReject(view: View){
        evaluationClick(false.toString())
    }

    @OnClick(R.id.button_dont_know)
    fun onClickDontKnow(view: View){
        if (pos < listUploads.size-1){
            pos++
            setImagesAndName()
        } else  {
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    @OnClick(R.id.button_accept)
    fun onClickAccept(view: View){
        evaluationClick(true.toString())
    }
}