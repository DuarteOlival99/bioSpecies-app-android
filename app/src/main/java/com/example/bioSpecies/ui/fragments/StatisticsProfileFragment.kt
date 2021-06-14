package com.example.bioSpecies.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.viewmodels.viewmodels.EstatisticasViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.MapViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.statistics_profile_fragment.*


class StatisticsProfileFragment : Fragment(){

    private lateinit var viewModel : EstatisticasViewModel

    var mAuth: FirebaseAuth? = null
    var currentUser : FirebaseUser? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null

    var mDesafiosDificeis : TextView? = null
    var mDesafiosMedios : TextView? = null
    var mDesafiosFaceis : TextView? = null
    var mKilometrosPercorridos : TextView? = null
    var mNumCapturas : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.statistics_profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(EstatisticasViewModel::class.java)
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

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDesafiosDificeis = view.findViewById(R.id.value_statistics_1)
        mDesafiosMedios = view.findViewById(R.id.value_statistics_2)
        mDesafiosFaceis = view.findViewById(R.id.value_statistics_3)
        //mKilometrosPercorridos = view.findViewById(R.id.value_statistics_4)
        mNumCapturas = view.findViewById(R.id.value_statistics_5)
    }

   override fun onStart() {
        super.onStart()

       //TODO alterar   -> utilizar dados da RealTime DataBase com o textView => mNumCapturas
        viewModel.atualizaCaptureEstatisticas()
        value_statistics_5.text = viewModel.getCaptureEstatics().toString()

        val estatisticasDesafios = viewModel.getEstatisticasDesafios()
        Log.i("estatisticasDesafios", estatisticasDesafios.toString())

        value_statistics_3.text = estatisticasDesafios.desafioDificeis.toString()
        value_statistics_2.text = estatisticasDesafios.desafioMedios.toString()
        value_statistics_1.text = estatisticasDesafios.desafioFacil.toString()

       currentUser = mAuth!!.currentUser

       /*

       // Read from the database
       mDatabaseRef?.addValueEventListener(object : ValueEventListener {
           override fun onDataChange(dataSnapshot: DataSnapshot) {
               // This method is called once with the initial value and again whenever data at this location is updated.

               val userID : String = currentUser?.uid.toString()
               val childUser = dataSnapshot.child("Users").child(userID)

               //atualizar desafios dificeis
               val desafiosDificeis: String = childUser.child("Estatisticas").child("desafios_faceis_completados").value.toString()
               mDesafiosDificeis?.setText(desafiosDificeis)

               //atualizar desafios medios
               val desafiosMedios: String = childUser.child("Estatisticas").child("desafios_medios_completados").value.toString()
               mDesafiosMedios?.setText(desafiosMedios)

               //atualizar desafios faceis
               val desafiosFaceis: String = childUser.child("Estatisticas").child("desafios_dificeis_completados").value.toString()
               mDesafiosFaceis?.setText(desafiosFaceis)

               //atualizar kilometros percorridos
               val kilometrosPercorridos: String = childUser.child("Estatisticas").child("kilometros_percorridos").value.toString()
               mKilometrosPercorridos?.setText(kilometrosPercorridos)

               //TODO
               //atualizar capturas realizadas
               //val capturasRealizadas: String = childUser.child("Estatisticas").child("capturas_realizadas").value.toString()
               //mNumCapturas?.setText(capturasRealizadas)

               Log.i("SPF->onStart->onData", "Valores atualizados com sucesso")
           }

           override fun onCancelled(error: DatabaseError) {
               // Failed to read value
               Log.i("PF->onStart->onCanc", "Failed to read value")
           }
       })


        */
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}