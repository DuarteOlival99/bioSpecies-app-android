package com.example.bioSpecies.domain.project

import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.ChallengeItem
import com.example.bioSpecies.data.local.list.ListStorage
import com.example.bioSpecies.data.sensors.battery.OnBatteryCurrentListener
import com.example.bioSpecies.domain.auth.LoginActivity
import com.example.bioSpecies.ui.adapters.ChallengesListAdapter
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.ChallengesViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.Blob
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.capture_history_fragment.*
import kotlinx.android.synthetic.main.challenges_atuais_fragment.*
import kotlinx.android.synthetic.main.challenges_atuais_fragment.view.*
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, OnBatteryCurrentListener {

    private lateinit var viewModel: ChallengesViewModel
    private var menuAtual = "MAPA"

    val nivelMaximo = 999

    var mAuth: FirebaseAuth? = null
    var currentUser : FirebaseUser? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setupDrawerMenu()
        when (item.itemId) {
            R.id.map -> {
                title = getText(R.string.map).toString()
                menuAtual = getText(R.string.map).toString()
                NavigationManager.goToMainScreen(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.avaliacoes -> {
                title = getText(R.string.avaliacoes).toString()
                menuAtual = getText(R.string.avaliacoes).toString()
                NavigationManager.goToEvaluation(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.avaliacoes_sem_nome -> {
                title = getText(R.string.atribuir_nome_de_foto_drawer).toString()
                menuAtual = getText(R.string.atribuir_nome_de_foto_drawer).toString()
                NavigationManager.goToEvaluationNoName(
                    supportFragmentManager
                )
                // invalidateOptionsMenu()
            }R.id.avaliacoes_sem_nome_game -> {
                title = getText(R.string.atribuir_nome_de_foto_game_drawer).toString()
                menuAtual = getText(R.string.atribuir_nome_de_foto_game_drawer).toString()
                NavigationManager.goToEvaluationNoNameGame(
                    supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.desafios -> {
                title = getText(R.string.desafios).toString()
                menuAtual = getText(R.string.desafios).toString()
                NavigationManager.goToChallenges(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.camera -> {
                title = getText(R.string.camera).toString()
                menuAtual = getText(R.string.camera).toString()
                NavigationManager.goToCamera(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.caderneta -> {
                title = getText(R.string.caderneta).toString()
                menuAtual = getText(R.string.caderneta).toString()
                NavigationManager.goToCaderneta(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.ranking -> {
                title = getText(R.string.ranking).toString()
                menuAtual = getText(R.string.ranking).toString()
                NavigationManager.goToRanking(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.perfil -> {
                title = getText(R.string.profile).toString()
                menuAtual = getText(R.string.profile).toString()
                NavigationManager.goToProfile(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.settings -> {
                title = getText(R.string.settings).toString()
                menuAtual = getText(R.string.settings).toString()
                NavigationManager.goToSettings(
                        supportFragmentManager
                )
                //invalidateOptionsMenu()
            }
            R.id.faq -> {
                title = getText(R.string.faq).toString()
                menuAtual = getText(R.string.faq).toString()
                NavigationManager.goToFaq(
                        supportFragmentManager
                )
                //invalidateOptionsMenu()
            }
            R.id.about -> {
                title = getText(R.string.about).toString()
                menuAtual = getText(R.string.about).toString()
                NavigationManager.goToAbout(
                        supportFragmentManager
                )
                // invalidateOptionsMenu()
            }
            R.id.logOut -> {
                title = getText(R.string.logOut).toString()
                menuAtual = getText(R.string.logOut).toString()
                mAuth?.signOut()
                //NavigationManager.goToLogin(supportFragmentManager)
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        when(item.itemId) {
            R.id.options_menu_desafios -> {
                mAuth = FirebaseAuth.getInstance()
                mDatabaseStorage = FirebaseDatabase.getInstance()
                mDatabaseRef = mDatabaseStorage!!.reference

                val adicionarXP = viewModel.verificaFotosEvaluatedComDesafios()
                Log.i( "adicionarXP" , adicionarXP.toString() )
                adicionarUserXP(adicionarXP)

                val listChallenges = viewModel.getChallengesAtuais()

                val dialogView = LayoutInflater.from(this).inflate(
                        R.layout.challenges_atuais_fragment,
                        null
                )
                //desenha a recycler view
                dialogView.challenges_atuais_lista.layoutManager =
                        LinearLayoutManager(this@MainActivity)
                dialogView.challenges_atuais_lista.adapter =
                        ChallengesListAdapter(
                                viewModel,
                                this@MainActivity,
                                R.layout.challenges_atuais_expression,
                                listChallenges as MutableList<ChallengeItem>
                        )

                val mBuilder = AlertDialog.Builder(this)
                        .setView(dialogView)

                val mAlertDialog = mBuilder.show()

                mAlertDialog.dialog_challenges_close.setOnClickListener {
                    mAlertDialog.dismiss()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    private fun adicionarUserXP(adicionarXP: Int) { // adicionar Xp ao user na firebase

        var nivelAtual: Int = 0
        var nivelSeguinte: Int = 0
        var xpAtual: Int = 0
        var xpMax: Int = 0

        // Read from the database
        mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called ONLY ONCE (addListenerForSingleValueEvent) with the initial value
                val userID: String = currentUser?.uid.toString()
                val childUser = dataSnapshot.child("Users").child(userID)

                //get dos valores da BD
                nivelAtual = childUser.child("Nivel").child("nivel_atual").value.toString().toInt()
                nivelSeguinte = nivelAtual + 1
                xpAtual = childUser.child("Nivel").child("xp_atual").value.toString().toInt()
                xpMax = childUser.child("Nivel").child("xp_max").value.toString().toInt()
                Log.i("CF->adicionarUserXP", "Dados lidos da BD: nivelAtual = $nivelAtual   nivelSeguinte = $nivelSeguinte   xpAtual = $xpAtual   xpMax = $xpMax")


                //adicionar xp e ver se passou de nivel
                var userRef = mDatabaseRef?.child("Users")?.child(currentUser?.uid.toString())
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ChallengesViewModel::class.java)
        NavigationManager.goToMainScreen(
                supportFragmentManager
        )
        setSupportActionBar(toolbar)
        setupDrawerMenu()

        mAuth = FirebaseAuth.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageRef = mFirebaseStorage!!.reference
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference

        currentUser = mAuth!!.currentUser


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var retVal = true
            retVal = Settings.System.canWrite(this)
            if (!retVal) {
                if (!Settings.System.canWrite(applicationContext)) {
                    val intent = Intent(
                            Settings.ACTION_MANAGE_WRITE_SETTINGS,
                            Uri.parse("package:$packageName")
                    )
                    Toast.makeText(
                            applicationContext,
                            "Please, allow system settings for automatic logout ",
                            Toast.LENGTH_LONG
                    ).show()
                    startActivityForResult(intent, 200)
                }
            } else {
//                Toast.makeText(
//                    applicationContext,
//                    "You are not allowed to wright ",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        }

        if (!screenRotated(savedInstanceState)) {
            NavigationManager.goToMainScreen(
                    supportFragmentManager
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun screenRotated(savedInstanceState: Bundle?) : Boolean {
        return savedInstanceState != null
    }

    private fun setupDrawerMenu() {
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        )
        nav_drawer.setNavigationItemSelectedListener(this)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        }
        super.onBackPressed()
    }

    override fun onCurrentChanged(current: Double) {
        Log.i("current", current.toString())
    }


    override fun onStart() {
        super.onStart()

        // Read from the database
        mDatabaseRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                val userID: String = currentUser?.uid.toString()
                val childUser = dataSnapshot.child("Users").child(userID)

                atualizarDados(childUser)

                Log.i("MA->onStart->onData", "Valores atualizados com sucesso")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i("MA->onStart->onCanc", "Failed to read value")
            }
        })



    }

    private fun atualizarDados(childUser: DataSnapshot) {
        //atualizar a foto do utilizador no drawer
        val profile_photo: de.hdodenhof.circleimageview.CircleImageView = nav_drawer.getHeaderView(0).findViewById(R.id.drawer_photo) as de.hdodenhof.circleimageview.CircleImageView
        var imageRef = mStorageRef?.child("images/UserProfileImages/" + currentUser?.uid + ".png")
        val ONE_MEGABYTE: Long = 1024 * 1024
        imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
            //converter ByteArray em Bitmap
            var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)

            //colocar bitmap na imageview
            profile_photo.setImageBitmap(bmp_foto!!)
        })?.addOnFailureListener(OnFailureListener {
            //Toast.makeText(applicationContext, "Erro a fazer load da imagem de perfil no drawer", Toast.LENGTH_LONG).show()
        })


        //atualizar o username do utilizador  no drawer
        val nomeUser: String = childUser.child("Username").value.toString()
        val profile_username: TextView = nav_drawer.getHeaderView(0).findViewById(R.id.drawer_title) as TextView
        profile_username.setText(nomeUser)


        //atualizar o nivel do utilizador  no drawer
        val nivelAtual: Int = childUser.child("Nivel").child("nivel_atual").value.toString().toInt()
        val nivelSeguinte: Int = nivelAtual + 1
        val xpAtual: Int = childUser.child("Nivel").child("xp_atual").value.toString().toInt()
        val xpMax: Int = childUser.child("Nivel").child("xp_max").value.toString().toInt()
        val progresso: Int = xpAtual * 100 / xpMax
        val xpFalta: Int = xpMax - xpAtual

        val profile_nivel_atual: TextView = nav_drawer.getHeaderView(0).findViewById(R.id.nivel_atual) as TextView
        profile_nivel_atual.setText(nivelAtual.toString())

        val profile_nivel_seguinte: TextView = nav_drawer.getHeaderView(0).findViewById(R.id.nivel_seguinte) as TextView
        profile_nivel_seguinte.setText(nivelSeguinte.toString())

        val profile_progress_bar: ProgressBar = nav_drawer.getHeaderView(0).findViewById(R.id.progress_bar_nivel) as ProgressBar
        profile_progress_bar.progress = progresso

        val profile_xp_falta: TextView = nav_drawer.getHeaderView(0).findViewById(R.id.quantidade_xp_nivel_seguinte_2) as TextView
        profile_xp_falta.setText(xpFalta.toString())
    }


}