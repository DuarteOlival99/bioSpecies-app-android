package com.example.bioSpecies.domain.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.*
import com.example.bioSpecies.domain.project.MainActivity
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class RegisterFragment : Fragment() {

    //private lateinit var viewModel : RegisterViewModel
    lateinit var viewModelSplashScreen: SplashScreenViewModel
    var mAuth: FirebaseAuth? = null
    private var mUsername: EditText? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mConfirmPassword: EditText? = null

    var currentUser : FirebaseUser? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null

    //val imageRef = Firebase.storage.reference

    val TAG = "RegisterFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.register_fragment, container, false)
        //viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        viewModelSplashScreen = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        ButterKnife.bind(this, view)

        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference

    }

    override fun onStart() {
        super.onStart()

    }

    fun getData(){
        getAnimaisFirebase()
        listFiles()
        listFilesWithNoName()
    }


    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mUsername = view.findViewById(R.id.register_username_text)
        mEmail = view.findViewById(R.id.register_email_text)
        mPassword = view.findViewById(R.id.register_password_text)
        mConfirmPassword = view.findViewById(R.id.register_confirm_password_text)
    }

    private fun listFilesWithNoName() = CoroutineScope(Dispatchers.IO).launch {
        val imageRef = Firebase.storage.reference
        try {
            val images = imageRef.child("images/camera/photosWithNoName/").listAll().await()
           // Log.i("imagesNoName", images.toString())
            var listImages = mutableListOf<PhotosNoName>()
            for(image in images.items) {
                val url = image.downloadUrl.await()
                val title = image.name.split("-||-").map { it -> it.trim() }
                var listPhotosNomeAtribuido = mutableListOf<FotoNomeAtribuido>()
                var listPhotosNomeAtribuidoGame = mutableListOf<FotoNomeAtribuidoGame>()
                var imageNoName = PhotosNoName(image.name, null, url.toString(), listPhotosNomeAtribuido, listPhotosNomeAtribuidoGame,  "", getBitmapFromURL(url.toString()), false, -1, false)
                image.metadata.addOnSuccessListener {
                    val data = it.creationTimeMillis
                    val userMetadata =  it.getCustomMetadata("user")
                    val avaliadaMetadata =  it.getCustomMetadata("avaliada?")
                    val userJaViuMetadata =  it.getCustomMetadata("userJaViu?")
                    val resultadoMetadata =  it.getCustomMetadata("resultado")
                    val nomesAtribuidosMetadata = it.getCustomMetadata("nomesAtribuidos")
                    val userNomesAtribuidosMetadata = it.getCustomMetadata("userNomesAtribuidos")

                    if (userMetadata != null) {
                        imageNoName.userID = userMetadata.toString()
                        if (userMetadata != mAuth?.uid) {
                            if (avaliadaMetadata != null) {
                                imageNoName.avaliada = avaliadaMetadata.toBoolean()
                            }
                            if (userJaViuMetadata != null) {
                                imageNoName.userJaViu = userJaViuMetadata.toBoolean()
                            }
                            if (resultadoMetadata != null) {
                                imageNoName.resultado = resultadoMetadata?.toInt()
                            }
                            if (data != null) {
                                imageNoName.data = getDate(data, "dd/MM/yyyy hh:mm:ss")
                            }

                            if (nomesAtribuidosMetadata != null && userNomesAtribuidosMetadata != null) {
                                val partsNome = nomesAtribuidosMetadata.split(";")
                                val partsNomeUser = userNomesAtribuidosMetadata.split(";")
                                if (partsNome.size == partsNomeUser.size) {
                                    for (i in 0 until partsNome.size) {
                                        val atribuicao =
                                                FotoNomeAtribuido(partsNome[i], partsNomeUser[i])
                                        listPhotosNomeAtribuido.add(atribuicao)
                                    }
                                    imageNoName.listAnimalName = listPhotosNomeAtribuido
                                }
                            }
                        }
                    }

                   // Log.i("meta149", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", "fail")
                }
                listImages.add(imageNoName)
            }
            //Log.i("listImages", listImages.toString())
            viewModelSplashScreen.setListNoNamePhotos(listImages)

        } catch (e: Exception) {
        }
    }

    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        val imageRef = Firebase.storage.reference
        try {
            val images = imageRef.child("images/camera/unevaluatedPhotos/").listAll().await()
            //Log.i("images", images.toString())
            var listImages = mutableListOf<Photos>()
            for(image in images.items) {
                val url = image.downloadUrl.await()
                val title = image.name.split("-||-").map { it -> it.trim() }
                var listPhotosEvaluation = mutableListOf<PhotoEvaluation>()
                var imageUnevaluated = Photos(image.name, null, url.toString(), title[0], listPhotosEvaluation, "", getBitmapFromURL(url.toString()), false, -1, false)
                image.metadata.addOnSuccessListener {
                    val data = it.creationTimeMillis
                    val userMetadata =  it.getCustomMetadata("user")
                    val avaliadaMetadata =  it.getCustomMetadata("avaliada?")
                    val userJaViuMetadata =  it.getCustomMetadata("userJaViu?")
                    val resultadoMetadata =  it.getCustomMetadata("resultado")
                    val photosEvaluationMetadata =  it.getCustomMetadata("photosEvaluation")
                    val photosEvaluationUserMetadata =  it.getCustomMetadata("photosEvaluationUser")

                    if (userMetadata != null){
                        imageUnevaluated.userID = userMetadata.toString()
                    }
                    if (avaliadaMetadata != null){
                        imageUnevaluated.avaliada = avaliadaMetadata.toBoolean()
                    }
                    if (userJaViuMetadata != null){
                        imageUnevaluated.userJaViu = userJaViuMetadata.toBoolean()
                    }
                    if (resultadoMetadata != null){
                        imageUnevaluated.resultado = resultadoMetadata?.toInt()
                    }
                    if (data != null){
                        imageUnevaluated.data = getDate(data, "dd/MM/yyyy hh:mm:ss")
                    }
                    if (photosEvaluationMetadata != null && photosEvaluationUserMetadata != null) {
                        val partsNome = photosEvaluationMetadata.split(";")
                        val partsNomeUser = photosEvaluationUserMetadata.split(";")
                        if (partsNome.size == partsNomeUser.size) {
                            for (i in 0 until partsNome.size) {
                                val atribuicao =
                                        PhotoEvaluation(partsNome[i], partsNomeUser[i])
                                listPhotosEvaluation.add(atribuicao)
                            }
                            imageUnevaluated.listPhotoEvaluation = listPhotosEvaluation
                        }
                    }
                  //  Log.i("meta208", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", "fail")
                }
                listImages.add(imageUnevaluated)
            }
           // Log.i("listImages", listImages.toString())
            viewModelSplashScreen.setListUnevaluatedPhotos(listImages)
        } catch (e: Exception) {
        }
    }
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    fun getDate(milliSeconds: Long, dateFormat: String?): Date? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        val dateString = formatter.format(calendar.time)
        return formatter.parse(dateString)
    }


    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            Log.i("getBitmapFromURL_Error", e.toString())
            null
        }
    }

    fun getAnimaisFirebase() {
        val listAnimalFirebase = mutableListOf<Animal>()
        val mDatabaseRef : DatabaseReference = mDatabaseStorage!!.getReference("animais/")
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator: Iterable<DataSnapshot> = dataSnapshot.children
                val iterator = snapshotIterator.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next().value.toString()
                    val parts = item.split(",")

                    val nomeTradicional = parts[0].split("=")[1]
                    val nomeCientifico = parts[37].split("=")[1].split("}")[0]
                    val classe = parts[1].split("=")[1]
                    val taxonID = parts[35].split("=")[1].toInt()
                    val fotoMediumUrl = parts[34].split("=")[1]
                    val totalObservationsResults = parts[33].split("=")[1].toInt()
                    val raridade = parts[32].split("=")[1].toInt()
                    val uuid = parts[36].split("=")[1]

                    //Log.i("part2", parts[2])
                    //val latitude = parts[2].split("=")[2].split("}")
                    val localizacao1 = Coordenadas(latitude = parts[2].split("=")[2].split("}")[0].toDouble(), longitude = parts[4].split("=")[1].split("}")[0].toDouble() )
                    localizacao1.uuid = parts[3].split("=")[1]
                    val localizacao2 = Coordenadas(latitude = parts[5].split("=")[1].split("}")[0].toDouble(), longitude = parts[7].split("=")[1].split("}")[0].toDouble() )
                    localizacao2.uuid = parts[6].split("=")[1]
                    val localizacao3 = Coordenadas(latitude = parts[8].split("=")[1].split("}")[0].toDouble(), longitude = parts[10].split("=")[1].split("}")[0].toDouble() )
                    localizacao3.uuid = parts[9].split("=")[1]
                    val localizacao4 = Coordenadas(latitude = parts[11].split("=")[1].split("}")[0].toDouble(), longitude = parts[13].split("=")[1].split("}")[0].toDouble() )
                    localizacao4.uuid = parts[12].split("=")[1]
                    val localizacao5 = Coordenadas(latitude = parts[14].split("=")[1].split("}")[0].toDouble(), longitude = parts[16].split("=")[1].split("}")[0].toDouble() )
                    localizacao5.uuid = parts[15].split("=")[1]
                    val localizacao6 = Coordenadas(latitude = parts[17].split("=")[1].split("}")[0].toDouble(), longitude = parts[19].split("=")[1].split("}")[0].toDouble() )
                    localizacao6.uuid = parts[18].split("=")[1]
                    val localizacao7 = Coordenadas(latitude = parts[20].split("=")[1].split("}")[0].toDouble(), longitude = parts[22].split("=")[1].split("}")[0].toDouble() )
                    localizacao7.uuid = parts[21].split("=")[1]
                    val localizacao8 = Coordenadas(latitude = parts[23].split("=")[1].split("}")[0].toDouble(), longitude = parts[25].split("=")[1].split("}")[0].toDouble() )
                    localizacao8.uuid = parts[24].split("=")[1]
                    val localizacao9= Coordenadas(latitude = parts[26].split("=")[1].split("}")[0].toDouble(), longitude = parts[28].split("=")[1].split("}")[0].toDouble() )
                    localizacao9.uuid = parts[27].split("=")[1]
                    val localizacao10 = Coordenadas(latitude = parts[29].split("=")[1].split("}")[0].toDouble(), longitude = parts[31].split("=")[1].split("}")[0].toDouble() )
                    localizacao10.uuid = parts[30].split("=")[1]

                    var listLocalizacoes = mutableListOf<Coordenadas>()
                    listLocalizacoes.add(localizacao1)
                    listLocalizacoes.add(localizacao2)
                    listLocalizacoes.add(localizacao3)
                    listLocalizacoes.add(localizacao4)
                    listLocalizacoes.add(localizacao5)
                    listLocalizacoes.add(localizacao6)
                    listLocalizacoes.add(localizacao7)
                    listLocalizacoes.add(localizacao8)
                    listLocalizacoes.add(localizacao9)
                    listLocalizacoes.add(localizacao10)

                    val animal = Animal(nomeTradicional, nomeCientifico, classe, taxonID, fotoMediumUrl, totalObservationsResults, raridade, listLocalizacoes, false)
                    animal.uuid = uuid

                    //listAnimalFirebase.add(animal)

                    viewModelSplashScreen.addAnimal(animal)

                    // val animal: Animal? = iterator.next().value as Animal?
                   // Log.i(TAG, "Item = " + item.toString()  )
                    //Log.i(TAG, "parts = " + parts.toString()  )

                }
                viewModelSplashScreen.atualizalistaAnimaisClasse()
                addListaAnimaisUser()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })

        //viewModel.updateListAnimal(listAnimalFirebase)

    }

    fun addListaAnimaisUser(){
        val list = viewModelSplashScreen.getAllAnimals()
        Log.i("listaAnimais", list.toString())
        val listUserAnimal = mutableListOf<UserAnimal>()
        var userRef = mDatabaseRef?.child("Users")?.child(currentUser?.uid.toString())?.child("Animal")
        for (animal in list){
            val userAnimal = listUserAnimal.add(UserAnimal(animal.nomeTradicional, animal.descoberto))
            userRef?.push()?.setValue(userAnimal)
        }

    }

    @OnClick(R.id.createAccountButton)
    fun onClickRegister(view: View){
        val username = mUsername?.text.toString().trim()
        val email = mEmail?.text.toString().trim()
        val password = mPassword?.text.toString().trim()
        val confirmPassword = mConfirmPassword?.text.toString().trim()

        //verificar se a password == confirmPassword, e se campos não estão vazios
        if( username.isEmpty() ){
            Toast.makeText(context, "Username vazio", Toast.LENGTH_LONG).show()
        } else if( email.isEmpty() ){
            Toast.makeText(context, "Email vazio", Toast.LENGTH_LONG).show()
        } else if( password.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword) ){
            Toast.makeText(context, "Erro na password", Toast.LENGTH_LONG).show()
        } else{

            mAuth!!.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener((context as Activity?)!!,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = mAuth!!.currentUser

//                            val mDatabaseRefRankingAvaliacoes : DatabaseReference = mDatabaseStorage!!.getReference("ranking_Nr_Capturas/")
//                            val userId = mAuth?.uid.toString()
//                            val childUserNovo = mDatabaseRefRankingAvaliacoes.child(userId)
//                            childUserNovo.child("nrCapturas").setValue(0)
//                            childUserNovo.child("userUUID").setValue(userId)

                            //criar e guarda todos os dados iniciais na Realtime DataBase
                            criarDadosNovoUser(username)

                            //Log.i("RegisterSuccessful", "createUserWithEmail:success and username updated")

                            getData()

                            //iniciar Main Activity
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("RegisterError", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    })

        }

    }

    private fun criarDadosNovoUser(username: String) {

        //Store data in database
        var userRef = mDatabaseRef?.child("Users")?.child(currentUser?.uid.toString())

        userRef?.child("Nivel")?.child("xp_atual")?.setValue(0)
        userRef?.child("Nivel")?.child("xp_max")?.setValue(100)
        userRef?.child("Nivel")?.child("nivel_atual")?.setValue(1)

        userRef?.child("Username")?.setValue(username)

        userRef?.child("Estatisticas")?.child("desafios_faceis_completados")?.setValue(0)
        userRef?.child("Estatisticas")?.child("desafios_medios_completados")?.setValue(0)
        userRef?.child("Estatisticas")?.child("desafios_dificeis_completados")?.setValue(0)
        userRef?.child("Estatisticas")?.child("kilometros_percorridos")?.setValue(0)
        userRef?.child("Estatisticas")?.child("capturas_realizadas")?.setValue(0)

        userRef?.child("Estatisticas")?.child("capturas_aceites")?.setValue(0)
        userRef?.child("Estatisticas")?.child("nr_avaliacoes")?.setValue(0)
        userRef?.child("Estatisticas")?.child("nr_avaliacoes_nome_atribuido")?.setValue(0)
        userRef?.child("Estatisticas")?.child("nr_avaliacoes_jogo")?.setValue(0)




        //TODO criar mais
        //userRef?.child("Desafios_atuais")?.child("desafios_faceis")?.child("")?.setValue(0)
    }
}