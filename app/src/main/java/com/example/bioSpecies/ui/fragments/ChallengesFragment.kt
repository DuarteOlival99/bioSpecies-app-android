package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.ChallengeItem
import com.example.bioSpecies.ui.adapters.ChallengesListAdapter
import com.example.bioSpecies.ui.viewmodels.viewmodels.ChallengesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.challenges_atuais_fragment.view.*
import kotlinx.android.synthetic.main.challenges_fragment.*


class ChallengesFragment : Fragment(){

    private lateinit var viewModel : ChallengesViewModel

    val nivelMaximo = 999

    var mAuth: FirebaseAuth? = null
    var currentUser : FirebaseUser? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.challenges_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ChallengesViewModel::class.java)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference
    }

   override fun onStart() {
        super.onStart()

       currentUser = mAuth!!.currentUser

       val adicionarXP = viewModel.verificaFotosEvaluatedComDesafios()

       Log.i( "adicionarXP" , adicionarXP.toString() )

       adicionarUserXP(adicionarXP)

       val listChallenges = viewModel.getChallengesAtuais()
       challenges_fragment_atuais_lista.layoutManager = LinearLayoutManager(context as Activity)
       challenges_fragment_atuais_lista.adapter =
               ChallengesListAdapter(
                       viewModel,
                       context as Activity,
                       R.layout.challenges_atuais_expression,
                       listChallenges as MutableList<ChallengeItem>
               )
    }

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

    override fun onDestroy() {
        super.onDestroy()
    }

    @OnClick(
            R.id.expand_current_challenges,
            R.id.button_current_challenges
    )
    fun clickCC(view: View){
        if (expand_current_challenges.contentDescription == "collapse"){
            expand_current_challenges.contentDescription = "expand"
            expand_current_challenges.setImageResource(R.drawable.ic_expand)
            //text_current_challenges.text = ""
            challenges_fragment_atuais_lista.visibility = View.GONE
        } else {
            expand_current_challenges.contentDescription = "collapse"
            expand_current_challenges.setImageResource(R.drawable.ic_collapse)
            //text_current_challenges.text = "blalalallaalal"
            val listChallenges = viewModel.getChallengesAtuais()
            challenges_fragment_atuais_lista.visibility = View.VISIBLE

        }
    }

}