package com.example.bioSpecies.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.ui.viewmodels.viewmodels.CadernetaViewModel
import kotlinx.android.synthetic.main.caderneta_expression.view.*
import kotlinx.android.synthetic.main.especie_perfil.*
import java.io.IOException
import java.util.*


class EspeciePerfilFragment() : Fragment() {

    private lateinit var viewModel : CadernetaViewModel

    private var animal : Animal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.especie_perfil, container, false)
        viewModel = ViewModelProviders.of(this).get(CadernetaViewModel::class.java)
        animal = viewModel.getAnimalSelected()
        ButterKnife.bind(this, view)
        return view
    }

    override fun onStart() {
        super.onStart()

        perfil_especie_nome.text = animal?.nomeTradicional
        perfil_especie_nome_cientifico_text.text = animal?.nomeCientifico
        perfil_especie_classe_text.text = animal?.classe
        perfil_especie_raridade_text.text = ""
        when (animal?.raridade){
            0 -> {
                perfil_especie_raridade_text.text = getString(R.string.common)
            }
            1 -> {
                perfil_especie_raridade_text.text = getString(R.string.not_rare)
            }
            2 -> {
                perfil_especie_raridade_text.text = getString(R.string.rare)
            }
            3 -> {
                perfil_especie_raridade_text.text = getString(R.string.very_rare)
            }
        }

        //perfil_especie_sobre_text.text = ""
        //perfil_especie_habitat_text.text = ""

        var localizacoesComunsResult : String = ""
        var listLocalizacoesComuns = mutableListOf<String>()
        for (localizacao in animal?.localizacao!!){
            val l = getAddress(localizacao.latitude, localizacao.longitude)
            if (!listLocalizacoesComuns.contains(l)){
                listLocalizacoesComuns.add(l)
            }
        }
        for (localizacao in 0 until listLocalizacoesComuns.size){
            if( listLocalizacoesComuns[localizacao] != ""){
                localizacoesComunsResult += listLocalizacoesComuns[localizacao]
            }
            if (localizacao != listLocalizacoesComuns.size-1){
                localizacoesComunsResult += " , "
            }
        }

        perfil_especie_zonas_text.text = localizacoesComunsResult

        Glide.with(perfil_especie_photo).load(animal?.fotoMediumUrl).into(perfil_especie_photo)

    }

    fun getAddress(lat: Double, lng: Double) : String{
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
            val obj: Address = addresses[0]
            Log.v("IGA", "Address ${obj.adminArea}")
            var area = ""
            if (obj.adminArea != null){
                area += obj.adminArea
            }
            return area
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (e: IOException) {
            Log.i("getAddress", "getAddress")
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        return ""
    }


}