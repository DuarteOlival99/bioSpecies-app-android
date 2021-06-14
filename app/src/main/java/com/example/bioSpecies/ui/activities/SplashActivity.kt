    package com.example.bioSpecies.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.Coordenadas
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.domain.auth.LoginActivity
import com.example.bioSpecies.ui.viewmodels.viewmodels.SettingsViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.settings_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : AppCompatActivity() {
    private val TAG = "SplashActivity"
    lateinit var viewModel: SplashScreenViewModel
    lateinit var viewModelSettings: SettingsViewModel

    val imageRef = Firebase.storage.reference
    var listImagesFinal = mutableListOf<Photos>()
    private var mDatabaseStorage = FirebaseDatabase.getInstance()

    var listAnimal = mutableListOf<Animal>()

    lateinit var comum: String
    lateinit var poucoRaro: String
    lateinit var raro: String
    lateinit var muitoRaro: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comum = getString(R.string.desafio_animal_comum)
        poucoRaro = getString(R.string.desafio_aninmal_pouco_raro)
        raro = getString(R.string.desafio_animal_raro)
        muitoRaro = getString(R.string.desafio_animal_muito_raro)

        setContentView(R.layout.activity_splash)
        viewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        viewModelSettings = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        Log.i("SplashActivity", "entrou")
       // viewModel.getAnimals()
        //viewModel.getAnimalsplaces()
        //listFiles()
        //getAnimaisFirebase()
        splash()

    }

    override fun onStart() {
        super.onStart()

    }

    fun splash(){
        val duration = 2000L
        val handle = Handler()
        handle.postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, duration)
    }

    fun getAnimaisFirebase() {
        val listAnimalFirebase = mutableListOf<Animal>()
        val mDatabaseRef : DatabaseReference = mDatabaseStorage.getReference("animais/")
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

                    viewModel.addAnimal(animal)

                   // val animal: Animal? = iterator.next().value as Animal?
                    Log.i(TAG, "Item = " + item.toString()  )
                    //Log.i(TAG, "parts = " + parts.toString()  )

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException())
            }
        })

        //viewModel.updateListAnimal(listAnimalFirebase)

    }

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