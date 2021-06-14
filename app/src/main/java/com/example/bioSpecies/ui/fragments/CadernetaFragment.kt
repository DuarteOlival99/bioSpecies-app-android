package com.example.bioSpecies.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.adapters.CadernetaListaClassesAdapter
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.CadernetaViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import kotlinx.android.synthetic.main.caderneta_fragment.*
import kotlinx.android.synthetic.main.challenges_atuais_fragment.view.*


class CadernetaFragment : Fragment(){

    private lateinit var viewModel : CadernetaViewModel
    private lateinit var viewModelSplashScreen: SplashScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.caderneta_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(CadernetaViewModel::class.java)
        viewModelSplashScreen = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

   override fun onStart() {
        super.onStart()
       viewModelSplashScreen.atualizalistaAnimaisClasse()

       //desenha a recycler view
       caderneta_lista_classes.layoutManager =
               LinearLayoutManager(context)
       caderneta_lista_classes.adapter =
               CadernetaListaClassesAdapter(
                       viewModel,
                       context,
                       R.layout.caderneta_list_expression,
                       viewModel.getListaNomesClasses() as MutableList<String>
               )

    }

    override fun onDestroy() {
        super.onDestroy()
    }



}