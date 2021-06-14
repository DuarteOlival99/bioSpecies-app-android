package com.example.bioSpecies.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.CadernetaItem
import com.example.bioSpecies.ui.adapters.CadernetaListAnimal1Adapter
import com.example.bioSpecies.ui.viewmodels.viewmodels.CadernetaViewModel
import kotlinx.android.synthetic.main.caderneta_fragment.*
import kotlinx.android.synthetic.main.caderneta_group_fragment.*


class CadernetaListaFragment : Fragment(){

    private lateinit var viewModel : CadernetaViewModel

    private lateinit var listaAnimaisCompleta : MutableList<Animal>

    private lateinit var listaAnimais0 : MutableList<Animal>
    private lateinit var listaAnimais1 : MutableList<Animal>
    private lateinit var listaAnimais2 : MutableList<Animal>
    private lateinit var listaAnimais3 : MutableList<Animal>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.caderneta_group_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(CadernetaViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

   override fun onStart() {
        super.onStart()

        clearListas()
        getList()
        updateCadernetaList()

   }

    private fun clearListas() {
        listaAnimais0 = mutableListOf<Animal>()
        listaAnimais1 = mutableListOf<Animal>()
        listaAnimais2 = mutableListOf<Animal>()
        listaAnimais3 = mutableListOf<Animal>()
    }

    private fun updateCadernetaList() {

        caderneta_lista_1.layoutManager = GridLayoutManager(activity, 4)
        caderneta_lista_1.adapter =
                CadernetaListAnimal1Adapter(
                        viewModel,
                        activity as Context,
                        R.layout.caderneta_expression,
                        listaAnimaisCompleta
                )

    }

    fun getList(){
        val listaAnimais : List<Animal> = viewModel.getListaAnimal()

        listaAnimaisCompleta = listaAnimais as MutableList<Animal>

        getLista(listaAnimais)
    }

    fun getLista(listaAnimais: List<Animal>) {
        val tamanho = listaAnimais.size
        var j = 0
        var listaParaAdicionar = 0

        while (j < tamanho) {

            when (listaParaAdicionar) { // em que lista adiciona
                0 -> { // adiciona na lista 0
                    listaAnimais0.add(listaAnimais[j])
                    j += 1
                    listaParaAdicionar = 1 //proxima lista a adicionar = 1
                }
                1 -> { // adiciona na lista 1
                    listaAnimais1.add(listaAnimais[j])
                    j += 1
                    listaParaAdicionar = 2 //proxima lista a adicionar = 2
                }
                2 -> { // adiciona na lista 2
                    listaAnimais2.add(listaAnimais[j])
                    j += 1
                    listaParaAdicionar = 3 //proxima lista a adicionar = 3
                }
                3 -> { // adiciona na lista 3
                    listaAnimais3.add(listaAnimais[j])
                    j += 1
                    listaParaAdicionar = 0 //proxima lista a adicionar = 0
                }
            }
        }

        Log.i("getListaAnimais0", listaAnimais0.toString())
        Log.i("getListaAnimais1", listaAnimais1.toString())
        Log.i("getListaAnimais2", listaAnimais2.toString())
        Log.i("getListaAnimais3", listaAnimais3.toString())

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}