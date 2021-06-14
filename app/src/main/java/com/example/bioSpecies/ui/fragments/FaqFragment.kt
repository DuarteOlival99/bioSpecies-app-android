package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import kotlinx.android.synthetic.main.challenges_fragment.expand_current_challenges
import kotlinx.android.synthetic.main.faq_fragment.*


class FaqFragment : Fragment(){

    private lateinit var viewModel : ChallengesViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.faq_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ChallengesViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

   @RequiresApi(Build.VERSION_CODES.Q)
   override fun onStart() {
        super.onStart()

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           win_xp_text_faq.justificationMode = JUSTIFICATION_MODE_INTER_WORD
           marker_text_faq.justificationMode = JUSTIFICATION_MODE_INTER_WORD
           challenges_color_text_faq.justificationMode = JUSTIFICATION_MODE_INTER_WORD
           challenges_text_faq.justificationMode = JUSTIFICATION_MODE_INTER_WORD
           species_text_faq.justificationMode = JUSTIFICATION_MODE_INTER_WORD
       }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @OnClick(
            R.id.win_xp_button_faq,
            R.id.win_xp_button_arrow_faq
    )
    fun clickWinXp(view: View){
        if (win_xp_button_arrow_faq.contentDescription == "collapse"){
            win_xp_button_arrow_faq.contentDescription = "expand"
            win_xp_button_arrow_faq.setImageResource(R.drawable.ic_expand)
            win_xp_text_faq.visibility = View.GONE
        } else {
            win_xp_button_arrow_faq.contentDescription = "collapse"
            win_xp_button_arrow_faq.setImageResource(R.drawable.ic_collapse)
            win_xp_text_faq.visibility = View.VISIBLE
        }
    }

    @OnClick(
            R.id.marker_button_faq,
            R.id.marker_button_arrow_faq
    )
    fun clickMarker(view: View){
        if (marker_button_arrow_faq.contentDescription == "collapse"){
            marker_button_arrow_faq.contentDescription = "expand"
            marker_button_arrow_faq.setImageResource(R.drawable.ic_expand)
            marker_text_faq.visibility = View.GONE
        } else {
            marker_button_arrow_faq.contentDescription = "collapse"
            marker_button_arrow_faq.setImageResource(R.drawable.ic_collapse)
            marker_text_faq.visibility = View.VISIBLE
        }
    }

    @OnClick(
            R.id.challenges_color_button_faq,
            R.id.challenges_color_button_arrow_faq
    )
    fun clickChallenges(view: View){
        if (challenges_color_button_arrow_faq.contentDescription == "collapse"){
            challenges_color_button_arrow_faq.contentDescription = "expand"
            challenges_color_button_arrow_faq.setImageResource(R.drawable.ic_expand)
            challenges_color_text_faq.visibility = View.GONE
        } else {
            challenges_color_button_arrow_faq.contentDescription = "collapse"
            challenges_color_button_arrow_faq.setImageResource(R.drawable.ic_collapse)
            challenges_color_text_faq.visibility = View.VISIBLE
        }
    }

    @OnClick(
            R.id.challenges_button_faq,
            R.id.challenges_button_arrow_faq
    )
    fun clickChallengesButton(view: View){
        if (challenges_button_arrow_faq.contentDescription == "collapse"){
            challenges_button_arrow_faq.contentDescription = "expand"
            challenges_button_arrow_faq.setImageResource(R.drawable.ic_expand)
            challenges_text_faq.visibility = View.GONE
        } else {
            challenges_button_arrow_faq.contentDescription = "collapse"
            challenges_button_arrow_faq.setImageResource(R.drawable.ic_collapse)
            challenges_text_faq.visibility = View.VISIBLE
        }
    }

    @OnClick(
            R.id.species_button_faq,
            R.id.species_button_arrow_faq
    )
    fun clickSpecies(view: View){
        if (species_button_arrow_faq.contentDescription == "collapse"){
            species_button_arrow_faq.contentDescription = "expand"
            species_button_arrow_faq.setImageResource(R.drawable.ic_expand)
            species_text_faq.visibility = View.GONE
        } else {
            species_button_arrow_faq.contentDescription = "collapse"
            species_button_arrow_faq.setImageResource(R.drawable.ic_collapse)
            species_text_faq.visibility = View.VISIBLE
        }
    }


}