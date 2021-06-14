package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.RankingNrCapturasFinal
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.RankingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.caderneta_fragment.*
import kotlinx.android.synthetic.main.caderneta_group_fragment.*
import kotlinx.android.synthetic.main.challenges_atuais_fragment.*
import kotlinx.android.synthetic.main.challenges_atuais_fragment.view.*
import kotlinx.android.synthetic.main.ranking_nr_capturas_pop_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RankingListaFragment : Fragment(){

    private lateinit var viewModel : RankingViewModel

    var mDatabaseRef: DatabaseReference? = null
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mDatabaseStorage = FirebaseDatabase.getInstance()
    val imageRef = Firebase.storage.reference



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.ranking_choice_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(RankingViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabaseRef = mDatabaseStorage!!.reference
    }

   override fun onStart() {
        super.onStart()
        getRankingList()
   }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getRankingList(){

        val dialogView = LayoutInflater.from(context as Activity).inflate(
                R.layout.ranking_nr_capturas_pop_up,
                null
        )
        //desenha a recycler view

        val mBuilder = AlertDialog.Builder(context as Activity)
                .setView(dialogView)

        val mAlertDialog = mBuilder.show()

        getRankingPodio(mAlertDialog)
        getMyNrCaptures(mAlertDialog)

    }

    private fun getRankingPodio(mAlertDialog: AlertDialog) {
        var listRanking = mutableListOf<RankingNrCapturasFinal>()
        val mDatabaseRefPodio : DatabaseReference = mDatabaseStorage!!.getReference("ranking_Nr_Capturas/")
        mDatabaseRefPodio.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator: Iterable<DataSnapshot> = dataSnapshot.children
                val iterator = snapshotIterator.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next().value.toString()
                    Log.i("rankingNrCapturas", "Item = " + item.toString()  )
                    Log.i("listRanking", listRanking.size.toString()  )

                    if (listRanking.size < 3){
                        mAlertDialog.circularProgressbar.progress = 25 * listRanking.size
                        Log.i("listRanking.sizeIf ", listRanking.size.toString()  )
                        val parts = item.split(",")
                        Log.i("parts", parts.toString())
                        val a = parts[0].split("=")[1]
                        val b = parts[1].split("=")[1].split("}")[0].toInt()
                        var addRanking = RankingNrCapturasFinal( a , b , "")
                        val url = getImage(addRanking)
                        addRanking = RankingNrCapturasFinal( a , b , url.toString())
                        listRanking.add( addRanking )
                    }
                    Log.i("listRankingDps", listRanking.size.toString()  )

                    if (listRanking.size == 3){
                        viewModel.atualizalistRankingNrCapturas(listRanking)
                        mAlertDialog.dismiss()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i("rankingNrCapturas", "Failed to read value.", error.toException())
            }
        })
    }

    private fun getImage(user: RankingNrCapturasFinal) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val image = imageRef.child("images/UserProfileImages/${user.url}.png")
            val url = image.downloadUrl.await()
        } catch (e: Exception) {

        }
    }

    private fun getMyNrCaptures(mAlertDialog: AlertDialog) {
        Log.i("estatisticasCapturas", "2")
        mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called ONLY ONCE (addListenerForSingleValueEvent) with the initial value
                val userID: String = mAuth?.uid.toString()
                val childUser = dataSnapshot.child("Users").child(userID).child("Estatisticas")?.child("capturas_aceites")

                //adicionar xp e ver se passou de nivel
                val userRef = mDatabaseRef?.child("Users")?.child(userID)

                //set dos valores (nivelAtual, xpAtual, xpMax) na RealTime BD
                val value = childUser.value.toString()
                mAlertDialog.circularProgressbar.progress = 100
                viewModel.atualizaMyNrcapturas(value)

            }

            override fun onCancelled(error: DatabaseError) {
                //Do nothing (é preciso criar a função por causa do handler, mas não é usada)
            }
        })
    }

    @OnClick(R.id.group_ranking_nr_capturas_text,
            R.id.group_ranking_nr_capturas,
            R.id.group_ranking_nr_capturas_constraintlayout)
    fun onCLickRankingChoice(view: View){
        (context as AppCompatActivity).supportFragmentManager.let { NavigationManager.goToRankingNrCaptures(it) }
    }


}