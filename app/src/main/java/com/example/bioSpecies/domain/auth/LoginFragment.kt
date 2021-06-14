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
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class LoginFragment : Fragment() {

    //private lateinit var viewModel : LoginViewModel
    lateinit var viewModelSplashScreen: SplashScreenViewModel
    var mAuth: FirebaseAuth? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null

    var listImages = mutableListOf<Photos>()
    var listImagesUnevaluated = mutableListOf<Photos>()

    var mDatabaseRef: DatabaseReference? = null

    val imageRef = Firebase.storage.reference
    private var mDatabaseStorage = FirebaseDatabase.getInstance()

    lateinit var comum: String
    lateinit var poucoRaro: String
    lateinit var raro: String
    lateinit var muitoRaro: String

    var rankingVerifica = true


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        viewModelSplashScreen = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comum = getString(R.string.desafio_animal_comum)
        poucoRaro = getString(R.string.desafio_aninmal_pouco_raro)
        raro = getString(R.string.desafio_animal_raro)
        muitoRaro = getString(R.string.desafio_animal_muito_raro)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference

    }

    override fun onStart() {
        super.onStart()
        //introduzDadosAnimal()
        viewModelSplashScreen.clearListChallenges()
        viewModelSplashScreen.createListChallengesTest(comum, poucoRaro, raro, muitoRaro)
        viewModelSplashScreen.verificaSemana()
        viewModelSplashScreen.getEstatisticasDesafios()
    }


    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mEmail = view.findViewById(R.id.login_email_text)
        mPassword = view.findViewById(R.id.login_password_text)
    }

    fun introduzDadosAnimal(){
        // Write a message to the database
        val list : List<Animal> = viewModelSplashScreen.getAllAnimals()
        var count = 0
        for (animal in list){
            if ( animal.nomeTradicional != ""){
                val mDatabaseRef : DatabaseReference = mDatabaseStorage.getReference("animais/")
                mDatabaseRef.push().setValue(animal)
                count++
                Log.i("animal", animal.toString())
                Log.i("count animal", count.toString())
            }
        }

    }


    @OnClick(R.id.loginButton)
    fun onClickLogin(view: View){
        //Toast.makeText(context, "Login", Toast.LENGTH_LONG).show()

        val email = mEmail?.text.toString().trim()
        val password = mPassword?.text.toString().trim()

        if( email.isEmpty() ){
            Toast.makeText(context, "Email vazio", Toast.LENGTH_LONG).show()
        } else if( password.isEmpty() ){
            Toast.makeText(context, "Password vazia", Toast.LENGTH_LONG).show()
        } else {
            //executar o login (com firebase)
            mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener((context as Activity?)!!, OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success")
                            //val user = mAuth!!.currentUser
                            //Toast.makeText(context, user?.displayName, Toast.LENGTH_LONG).show()
                            listFiles()
                            listFilesEvaluate()
                            listFilesUnevaluate()
                            listFilesWithNoName()
                            listFilesWithNoNameGame()
                            viewModelSplashScreen.getAnimals()
                            startActivity(Intent(context, MainActivity::class.java))
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.i("Login Error", "signInWithEmail:failure", task.exception)
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }

                    })
        }
    }

    private fun listFilesWithNoNameGame() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = imageRef.child("images/camera/photosWithNoNameGame/").listAll().await()
            Log.i("imagesNoName", images.toString())
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
                    val nomesAtribuidosMetadataGame = it.getCustomMetadata("nomesAtribuidosGame")
                    val userNomesAtribuidosMetadataGame = it.getCustomMetadata("userNomesAtribuidosGame")

                    Log.i("userMetadataPhoto", "userMetadata")

                    if (userMetadata != null) {
                        Log.i("teste1", "1")
                        imageNoName.userID = userMetadata.toString()
                        if (userMetadata != mAuth?.uid) {
                            Log.i("teste2", "1")
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
                            if (nomesAtribuidosMetadataGame != null && userNomesAtribuidosMetadataGame != null) {
                                val partsNome = nomesAtribuidosMetadataGame.split(";")
                                val partsNomeUser = userNomesAtribuidosMetadataGame.split(";")
                                if (partsNome.size == partsNomeUser.size) {
                                    for (i in 0 until partsNome.size) {
                                        val atribuicao =
                                                FotoNomeAtribuidoGame(partsNome[i], partsNomeUser[i])
                                        listPhotosNomeAtribuidoGame.add(atribuicao)
                                    }
                                    imageNoName.listAnimalNameGame = listPhotosNomeAtribuidoGame
                                }
                            }

                            Log.i("photoCompleta", imageNoName.toString())

                        }else{
                            listImages.remove(imageNoName)
                        }
                    }else{
                        listImages.remove(imageNoName)
                    }

                    Log.i("metaFoto", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", "fail")
                }
                listImages.add(imageNoName)
            }
            Log.i("setListNoNamePhotos", listImages.toString())
            viewModelSplashScreen.setListNoNameGamePhotos(listImages)

        } catch (e: Exception) {
        }
    }

    private fun listFilesWithNoName() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = imageRef.child("images/camera/photosWithNoName/").listAll().await()
            Log.i("imagesNoName", images.toString())
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

                    Log.i("userMetadataPhoto", "userMetadata")


                    if (userMetadata != null) {
                        Log.i("teste1", "1")
                        imageNoName.userID = userMetadata.toString()
                        if (userMetadata != mAuth?.uid) {
                            Log.i("teste2", "1")
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

                            Log.i("photoCompleta", imageNoName.toString())


                            var r = false
                            for ( animal in imageNoName.listAnimalName){
                                if (animal.nomeAtribuidoUser == mAuth?.uid.toString()){
                                    r = true
                                }
                            }
                            if (r){
                                listImages.remove(imageNoName)
                                Log.i("fotoRemove", "fotoRemove")
                            }

                        }else{
                            listImages.remove(imageNoName)
                        }
                    }else{
                        listImages.remove(imageNoName)
                    }

                    Log.i("metaFoto", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", "fail")
                }
                listImages.add(imageNoName)
            }
            Log.i("setListNoNamePhotos", listImages.toString())
            viewModelSplashScreen.setListNoNamePhotos(listImages)

        } catch (e: Exception) {
        }
    }

    @OnClick(R.id.registerButton)
    fun onClickRegister(view: View){
        //Toast.makeText(context, "Register", Toast.LENGTH_LONG).show() //toast.;
        fragmentManager?.let { NavigationManager.goToRegister(it) }
    }

//    @OnClick(R.id.login_logo)
//    fun onClickLoginLogo(view: View){
//        //Toast.makeText(context, "Register", Toast.LENGTH_LONG).show() //toast.;
//        startActivity(Intent(context, MainActivity::class.java))
//    }


    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = imageRef.child("images/camera/unevaluatedPhotos/").listAll().await()
            Log.i("images", images.toString())
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
                    Log.i("meta380", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", "fail")
                }
                listImages.add(imageUnevaluated)
            }
            Log.i("listImages", listImages.toString())
            viewModelSplashScreen.setListUnevaluatedPhotos(listImages)
        } catch (e: Exception) {
        }
    }

    private fun listFilesEvaluate() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = imageRef.child("images/camera/evaluatedPhotos/").listAll().await()
            Log.i("images", images.toString())
            var listImages = mutableListOf<Photos>()
            val mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            Log.i("mAuth", mAuth?.uid.toString())
            var nrCapturasAceites = 0
            for(image in images.items) {
                var userMetadata: String? = ""
                val url = image.downloadUrl.await()
                Log.i("urlTeste", url.toString())
                val title = image.name.split("-||-").map { it -> it.trim() }
                var listPhotosEvaluation = mutableListOf<PhotoEvaluation>()
                var imageEvaluated = Photos(image.name, null, url.toString(), title[0], listPhotosEvaluation, "", getBitmapFromURL(url.toString()), false, -1, false)
                //listImages.add(imageEvaluated)
                image.metadata.addOnSuccessListener {
                    userMetadata =  it.getCustomMetadata("user")
                    val data = it.creationTimeMillis
                    val avaliadaMetadata =  it.getCustomMetadata("avaliada?")
                    val userJaViuMetadata =  it.getCustomMetadata("userJaViu?")
                    val resultadoMetadata =  it.getCustomMetadata("resultado")
                    val photosEvaluationMetadata =  it.getCustomMetadata("photosEvaluation")
                    val photosEvaluationUserMetadata =  it.getCustomMetadata("photosEvaluationUser")

                    if (userMetadata != null ){
                        imageEvaluated.userID = userMetadata.toString()
                        if (userMetadata == mAuth?.uid) {
                            if (avaliadaMetadata != null){
                                imageEvaluated.avaliada = avaliadaMetadata.toBoolean()
                            }
                            if (userJaViuMetadata != null){
                                imageEvaluated.userJaViu = userJaViuMetadata.toBoolean()
                            }
                            if (resultadoMetadata != null){
                                if ( resultadoMetadata.toInt() == 1){
                                    nrCapturasAceites++
                                }
                                imageEvaluated.resultado = resultadoMetadata.toInt()
                            }
                            if (data != null){
                                imageEvaluated.data = getDate(data, "dd/MM/yyyy hh:mm:ss")
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
                                    imageEvaluated.listPhotoEvaluation = listPhotosEvaluation
                                }
                            }
                            listImages.add(imageEvaluated)
                        }
                    }

                    val imagesSize = images.items.size
                    if (image == images.items[imagesSize - 1] ){
                        Log.i("estatisticasCapturas", "1")
                        atualizaEstatisticaCapturas(nrCapturasAceites)
                    }

                    Log.i("meta445", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", userMetadata.toString())
                }

            }

            Log.i("listFilesEvaluate", listImages.toString())
            viewModelSplashScreen.setListEvaluatedPhotos(listImages)
        } catch (e: Exception) {
            Log.i("erroStream", "erroStream")
            withContext(Dispatchers.Main) {
            }
        }
    }

    private fun atualizaEstatisticaCapturas(nrCapturasAceites: Int) {
        Log.i("estatisticasCapturas", "2")
        mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called ONLY ONCE (addListenerForSingleValueEvent) with the initial value
                val userID: String = mAuth?.uid.toString()
                val childUser = dataSnapshot.child("Users").child(userID)

                //adicionar xp e ver se passou de nivel
                val userRef = mDatabaseRef?.child("Users")?.child(userID)

                Log.i("nrCapturasAceites", nrCapturasAceites.toString())
                //set dos valores (nivelAtual, xpAtual, xpMax) na RealTime BD
                userRef?.child("Estatisticas")?.child("capturas_aceites")?.setValue(nrCapturasAceites)

                Log.i("estatisticasCapturas", "3")
                atualizaRankingNrCapturasFireBase(nrCapturasAceites)

            }

            override fun onCancelled(error: DatabaseError) {
                //Do nothing (é preciso criar a função por causa do handler, mas não é usada)
            }
        })
    }

    private fun atualizaRankingNrCapturasFireBase(nrCapturasAceites: Int){
        Log.i("estatisticasCapturas", "4")
        var listRanking = mutableListOf<RankingNrCapturas>()
        val mDatabaseRef : DatabaseReference = mDatabaseStorage!!.getReference("ranking_Nr_Capturas/")
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator: Iterable<DataSnapshot> = dataSnapshot.children
                val iterator = snapshotIterator.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next().value.toString()
                    Log.i("rankingNrCapturas", "Item = " + item.toString()  )
                    Log.i("listRanking", listRanking.size.toString()  )

                    if (listRanking.size < 3){
                        Log.i("listRanking.sizeIf ", listRanking.size.toString()  )
                        val parts = item.split(",")
                        Log.i("parts", parts.toString())
                        val a = parts[0].split("=")[1]
                        val b = parts[1].split("=")[1].split("}")[0].toInt()
                        val addRanking = RankingNrCapturas( a , b )
                        listRanking.add( addRanking )
                    }
                    Log.i("listRankingDps", listRanking.size.toString()  )

                    if (listRanking.size == 3){
                        Log.i("listRankingsizeElse ", listRanking.size.toString()  )
                        verificaRanking(listRanking, nrCapturasAceites) //verificar se este ta no top 3 ou passa a frente de alguem
                        Log.i("listRankingsizeElse ", listRanking.size.toString()  )
                    }

                    if(!iterator.hasNext() && listRanking.size < 3){
                        Log.i("testarIterator ", listRanking.size.toString())
                        val mDatabaseRefRankingAvaliacoes : DatabaseReference = mDatabaseStorage.getReference("ranking_Nr_Capturas/")
                        val mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                        val userId = mAuth?.uid.toString()
                        val childUser = mDatabaseRefRankingAvaliacoes.child(userId)
                        childUser.child("nrCapturas").setValue(nrCapturasAceites)
                        childUser.child("userUUID").setValue(userId)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i("rankingNrCapturas", "Failed to read value.", error.toException())
            }
        })

    }

    private fun verificaRanking(listRanking : List<RankingNrCapturas> , nrCapturasAceites: Int){
        if ( rankingVerifica ){

            var existeMinhaConta = false
            val mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            val userId = mAuth?.uid.toString()

            for (item in listRanking){
                if (item.userUUID == userId){
                    existeMinhaConta = true
                }
            }

            if (existeMinhaConta){
                val mDatabaseRefRankingAvaliacoes : DatabaseReference = mDatabaseStorage.getReference("ranking_Nr_Capturas/")
                val childUserNovo = mDatabaseRefRankingAvaliacoes.child(userId)
                childUserNovo.child("nrCapturas").setValue(nrCapturasAceites)
                childUserNovo.child("userUUID").setValue(userId)
            }else{
                var listSortRanking = mutableListOf<RankingNrCapturas>()
                listSortRanking = listRanking as MutableList<RankingNrCapturas>
                listSortRanking.sort()
                Log.i("listSortRanking", listSortRanking.toString())
                Log.i("nrCapturasAceites", nrCapturasAceites.toString())

                if (nrCapturasAceites > listSortRanking[0].nrCapturas){
                    val mDatabaseRefRankingAvaliacoes : DatabaseReference = mDatabaseStorage.getReference("ranking_Nr_Capturas/")
                    val childUserAntigo = mDatabaseRefRankingAvaliacoes.child(listSortRanking[0].userUUID)
                    childUserAntigo.removeValue()

                    val childUserNovo = mDatabaseRefRankingAvaliacoes.child(userId)
                    childUserNovo.child("nrCapturas").setValue(nrCapturasAceites)
                    childUserNovo.child("userUUID").setValue(userId)

                }
            }

            rankingVerifica = false
        }

    }

    private fun listFilesUnevaluate() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = imageRef.child("images/camera/unevaluatedPhotos/").listAll().await()
            Log.i("images", images.toString())
            var listImages = mutableListOf<Photos>()
            val mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            Log.i("mAuth", mAuth?.uid.toString())
            for(image in images.items) {
                var userMetadata: String? = ""
                val url = image.downloadUrl.await()
                val title = image.name.split("-||-").map { it -> it.trim() }
                var listPhotosEvaluation = mutableListOf<PhotoEvaluation>()
                var imageUnevaluated = Photos(image.name, null, url.toString(), title[0], listPhotosEvaluation, "", getBitmapFromURL(url.toString()), false, -1, false)
                //listImages.add(imageEvaluated)
                image.metadata.addOnSuccessListener {
                    userMetadata =  it.getCustomMetadata("user")
                    val data = it.creationTimeMillis
                    val avaliadaMetadata =  it.getCustomMetadata("avaliada?")
                    val userJaViuMetadata =  it.getCustomMetadata("userJaViu?")
                    val resultadoMetadata =  it.getCustomMetadata("resultado")
                    val photosEvaluationMetadata =  it.getCustomMetadata("photosEvaluation")
                    val photosEvaluationUserMetadata =  it.getCustomMetadata("photosEvaluationUser")

                    if (userMetadata != null ){
                        imageUnevaluated.userID = userMetadata.toString()
                        if (userMetadata == mAuth?.uid) {
                            if (avaliadaMetadata != null){
                                imageUnevaluated.avaliada = avaliadaMetadata.toBoolean()
                            }
                            if (userJaViuMetadata != null){
                                imageUnevaluated.userJaViu = userJaViuMetadata.toBoolean()
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
                            if (resultadoMetadata != null){
                                imageUnevaluated.resultado = resultadoMetadata.toInt()
                                if (resultadoMetadata.toInt() == -1){
                                    listImages.add(imageUnevaluated)
                                }
                            }
                        }
                    }
                    Log.i("meta513", userMetadata.toString())
                }.addOnFailureListener {
                    Log.i("fail", userMetadata.toString())
                }
            }
            Log.i("listFilesUnevaluate", listImages.toString())
            viewModelSplashScreen.setListMyUnevaluatedPhotos(listImages)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
            }
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

}