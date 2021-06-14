package com.example.bioSpecies.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.adapters.HistoricoListAdapter
import com.example.bioSpecies.ui.viewmodels.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.capture_history_fragment.*


class CapturesHistoryFragment : Fragment(){

    private lateinit var viewModel : ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.capture_history_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

   override fun onStart() {
        super.onStart()

       capture_history_lista.layoutManager = LinearLayoutManager(activity as Context)
       capture_history_lista.adapter =
           HistoricoListAdapter(
               viewModel,
               activity as Context,
               R.layout.capture_history_expression,
               viewModel.getCaptureHistory()
           )
    }

    override fun onDestroy() {
        super.onDestroy()
    }



}