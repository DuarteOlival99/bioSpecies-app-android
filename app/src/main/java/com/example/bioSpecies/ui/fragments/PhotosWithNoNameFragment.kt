package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.FotoNomeAtribuido
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
import kotlinx.android.synthetic.main.photos_with_no_name_fragment.*
import java.io.ByteArrayOutputStream

class PhotosWithNoNameFragment : Fragment(){
    private lateinit var viewModel: CameraViewModel
    private var imageView: ImageView? = null

    var pos = 0

    var mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var mFirebaseStorage = FirebaseStorage.getInstance()
    private var mDatabaseStorage = FirebaseDatabase.getInstance()

    private val storageRef = mFirebaseStorage.reference

    private var listUploadsNoName = listOf<PhotosNoName>()

    var currentUser : FirebaseUser? = null
    var mDatabaseRef: DatabaseReference? = null
    val nivelMaximo = 999


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.photos_with_no_name_fragment, container, false)
        imageView = view!!.findViewById<View>(R.id.foto_sem_nome) as ImageView
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
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

        val listAnimalsNames = viewModel.getlListAnimalsNames()
        val adapter = ArrayAdapter<String>(context as Activity , android.R.layout.simple_dropdown_item_1line, listAnimalsNames)
        foto_no_name_input.setAdapter(adapter)

        getImages()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun validarNomeEspecie() : Boolean{
        if (foto_no_name_input.text!!.isEmpty() || verificarNomeAnimal()){
            foto_no_name_input.error = "É necessario introduzir um nome de animal valido"
            return false
        }else{
            return true
        }
    }
    fun getImages(){
        var userID: String = FirebaseAuth.getInstance().currentUser!!.uid
        listUploadsNoName = viewModel.getListPhotosNoName()
        Log.i("listUploadsNoName", listUploadsNoName.toString())
        if (listUploadsNoName.isNotEmpty()){
            setImages()
        } else  {
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    fun verificarNomeAnimal() : Boolean{
        val nomeIntroduzido = foto_no_name_input.text.toString()
        val list = viewModel.getlListAnimalsNames()
        if (list.contains(nomeIntroduzido)){
            return false
        }
        return true
    }

    fun naoTemMaisAvaliacoes(){
        atribuir_nome.text = getString(R.string.nao_tem_mais_fotos)
        foto_sem_nome.setImageResource(R.drawable.ic__no_photography)
        constraint_butoes.visibility = View.GONE
        foto_no_name_input.visibility = View.GONE
        foto_no_name.visibility = View.GONE
    }

    fun setImages(){
        Log.i("setImages", "setImages")
        foto_sem_nome.setImageBitmap(listUploadsNoName[pos].image)
        foto_no_name_input.setText("")
    }

    @OnClick(R.id.button_dont_know_no_name)
    fun onClickDontKnow(view: View){
        nextPhoto()
    }

    fun nextPhoto(){
        if (listUploadsNoName.isNotEmpty()){
            Log.i("nextPhoto", "nextPhoto entrou")
            if (listUploadsNoName.size == 1){
                Toast.makeText(context, "Foto avaliada, proxima foto apresentada", Toast.LENGTH_LONG).show()
                viewModel.removePhotoEvaluate(listUploadsNoName[pos])
                naoTemMaisAvaliacoes()
            }else{
                viewModel.removePhotoEvaluate(listUploadsNoName[pos])
                listUploadsNoName = viewModel.getListPhotosNoName()
                setImages()
            }
        } else  {
            viewModel.removePhotoEvaluate(listUploadsNoName[pos])
            //fazer página a dizer que nao tem nada para avaliar
            naoTemMaisAvaliacoes()
        }
    }

    fun removePhotoLocation(animal : PhotosNoName){
        val photoRef = storageRef.child("images/camera/photosWithNoName/${animal.title}")
        // delete the file
        mFirebaseStorage.reference.child("images/camera/photosWithNoName/${animal.title}").delete()
    }


    fun addNewLocationPhoto(animal: PhotosNoName, animalUuidUser: String, animalNameAnswer: String){
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        animal.image?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val data = outputStream.toByteArray()

        var inputNameAnimal = animal.title

        val path: String = "images/camera/photosWithNoNameGame/"  + inputNameAnimal
        val mStorageRef: StorageReference = mFirebaseStorage.getReference(path)
        val mDatabaseRef: DatabaseReference =
                mDatabaseStorage.getReference("image/camera/photosWithNoNameGame/" + animal.userID + "/")

        val metadata: StorageMetadata = StorageMetadata.Builder()
                .setCustomMetadata("user", animal.userID)
                .setCustomMetadata("avaliada?", animal.avaliada.toString())
                .setCustomMetadata("resultado",  animal.resultado.toString())
                .setCustomMetadata("userJaViu?",  animal.userJaViu.toString())
                .setCustomMetadata("nomesAtribuidos", animalNameAnswer)
                .setCustomMetadata("userNomesAtribuidos", animalUuidUser)
                .setCustomMetadata("nomeMaisVotado", "")
                .setCustomMetadata("nomesAtribuidosGame", "")
                .setCustomMetadata("userNomesAtribuidosGame", "")
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
                    Toast.makeText(
                            context, "Avaliacao guardada", Toast.LENGTH_SHORT
                    ).show()

                    removePhotoLocation(animal)

                    nextPhoto()

                    Log.i("FBApp1_URL ", url.toString())
                })
    }

    fun addNewLocationPhotoEvaluated(animal: PhotosNoName, animalUuidUser: String, animalNameAnswer: String){
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        animal.image?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val data = outputStream.toByteArray()

        val nomeAtribuido = animalNameAnswer.split(";")[1]

        var inputNameAnimal = nomeAtribuido + animal.title

        val path: String = "images/camera/evaluatedPhotos/"  + inputNameAnimal
        val mStorageRef: StorageReference = mFirebaseStorage.getReference(path)
        val mDatabaseRef: DatabaseReference =
                mDatabaseStorage.getReference("image/camera/evaluatedPhotos/" + animal.userID + "/")

        val metadata: StorageMetadata = StorageMetadata.Builder()
                .setCustomMetadata("user", animal.userID)
                .setCustomMetadata("resultado", "1")
                .setCustomMetadata("avaliada?", "true")
                .setCustomMetadata("userJaViu?",  animal.userJaViu.toString())
                .setCustomMetadata("nomesAtribuidos", animalNameAnswer)
                .setCustomMetadata("userNomesAtribuidos", animalUuidUser)
                .setCustomMetadata("nomeMaisVotado", nomeAtribuido)
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
                    Toast.makeText(
                            context, "Avaliacao guardada", Toast.LENGTH_SHORT
                    ).show()

                    removePhotoLocation(animal)

                    nextPhoto()

                    Log.i("FBApp1_URL ", url.toString())
                })
    }


    fun updateMetadate(animalTitle: String, animalUuidUser: String, animalNameAnswer: String, resultado : Int){
        val photoRef = storageRef.child("images/camera/photosWithNoName/$animalTitle")
        val metadata = storageMetadata {
            setCustomMetadata("nomesAtribuidos", animalNameAnswer)
            setCustomMetadata("userNomesAtribuidos", animalUuidUser)
        }

        photoRef.updateMetadata(metadata).addOnSuccessListener {
            // Updated metadata is in storageMetadata
            val resultado =  it.getCustomMetadata("resultado")
        }.addOnFailureListener {
        }

        when (resultado) {
            1 -> {
                addNewLocationPhoto(listUploadsNoName[pos], animalUuidUser, animalNameAnswer)
            }
            2 -> {
                addNewLocationPhotoEvaluated(listUploadsNoName[pos], animalUuidUser, animalNameAnswer)
                adicionarUserXP(listUploadsNoName[pos].listAnimalName, 20)
            }
            else -> {
                Log.i("nextPhoto", "nextPhoto")
                nextPhoto()
            }
        }

    }

    private fun adicionarUserXP(photoEvaluation: List<FotoNomeAtribuido>, adicionarXP: Int) { // adicionar Xp ao user na firebase

        var nivelAtual: Int = 0
        var nivelSeguinte: Int = 0
        var xpAtual: Int = 0
        var xpMax: Int = 0

        var list = mutableListOf<FotoNomeAtribuido>()

        for (e in photoEvaluation){
            if (e.nomeAtribuidoUser != ""){
                list.add(e)
            }
        }

        for (evaluation in list){
                Log.i("adicionarXP", "entrou ${evaluation.nomeAtribuidoUser}")
                // Read from the database
                mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called ONLY ONCE (addListenerForSingleValueEvent) with the initial value
                        val userID: String = evaluation.nomeAtribuidoUser
                        val childUser = dataSnapshot.child("Users").child(userID)

                        //get dos valores da BD
                        nivelAtual = childUser.child("Nivel").child("nivel_atual").value.toString().toInt()
                        nivelSeguinte = nivelAtual + 1
                        xpAtual = childUser.child("Nivel").child("xp_atual").value.toString().toInt()
                        xpMax = childUser.child("Nivel").child("xp_max").value.toString().toInt()
                        Log.i("CF->adicionarUserXP", "Dados lidos da BD: nivelAtual = $nivelAtual   nivelSeguinte = $nivelSeguinte   xpAtual = $xpAtual   xpMax = $xpMax")

                        var userRef = mDatabaseRef?.child("Users")?.child(evaluation.nomeAtribuidoUser)

                        //atualiza dados estatistica
                        var estatiticaNrNomesAtribuido = childUser.child("Estatisticas").child("nr_avaliacoes_nome_atribuido").value.toString().toInt()
                        estatiticaNrNomesAtribuido++
                        userRef?.child("Estatisticas")?.child("nr_avaliacoes_nome_atribuido")?.setValue(estatiticaNrNomesAtribuido)

                        //adicionar xp e ver se passou de nivel
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


    @OnClick(R.id.button_send_answer)
    fun onClickSendAnswer(view: View){
        if (validarNomeEspecie()){
            var listNames = mutableListOf<FotoNomeAtribuido>()

            val lastName = foto_no_name_input.text.toString()
            val lastUser = mAuth?.uid.toString()
            val fotoName = FotoNomeAtribuido(lastName, lastUser)

            var animalNameAnswer = ""
            var animalAnswerUser = ""

            for (name in listUploadsNoName[pos].listAnimalName) {
                listNames.add(name)

                animalNameAnswer += name.nomeAtribuido
                animalNameAnswer += ";"

                animalAnswerUser += name.nomeAtribuidoUser
                animalAnswerUser += ";"
            }

            listNames.add(fotoName)
            animalNameAnswer += lastName
            animalAnswerUser += lastUser

            updateDataAnimals(listUploadsNoName[pos], listNames)
            if (listUploadsNoName[pos].listAnimalName.size == 5){
                Log.i("updateMetadate", "1")
                Log.i("listAnimalName", listUploadsNoName[pos].listAnimalName.toString())

                var result = true
                for (i in 1 until listUploadsNoName[pos].listAnimalName.size){
                    if (i != listUploadsNoName[pos].listAnimalName.size - 1){
                        for (j in i + 1 until listUploadsNoName[pos].listAnimalName.size){
                            if (listUploadsNoName[pos].listAnimalName[i].nomeAtribuido != listUploadsNoName[pos].listAnimalName[j].nomeAtribuido){
                                result = false
                                break
                            }
                            if (!result){
                                break
                            }
                        }
                    }
                }

                if (result){
                    updateMetadate(listUploadsNoName[pos].title, animalAnswerUser, animalNameAnswer, 2)
                }else{
                    updateMetadate(listUploadsNoName[pos].title, animalAnswerUser, animalNameAnswer, 1)
                }

            }else{
                Log.i("updateMetadate", "0")
                updateMetadate(listUploadsNoName[pos].title, animalAnswerUser, animalNameAnswer, 0)
            }
        }
    }

    private fun updateDataAnimals(photosNoName: PhotosNoName, listNames: MutableList<FotoNomeAtribuido>) {
        photosNoName.listAnimalName = listNames
    }
}