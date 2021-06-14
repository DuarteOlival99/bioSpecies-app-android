package com.example.bioSpecies.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.ui.viewmodels.viewmodels.MapViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.marker_map_details.view.*


class InfoWindowAdapter(val viewModel: MapViewModel, var context: Context, var layout: Int, val animalList: List<Animal>): GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker?): View? {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(layout, null)

        Log.i("markerTitle", marker?.title.toString())
        for (animal in animalList){
            if (animal.nomeTradicional == marker?.title){
                viewModel.setAnimalSelected(animal)
                Log.i("setAnimalSelected", animal.toString())
            }
        }
        view.markerName.text = marker?.title
        view.details.text = marker?.snippet
        return view
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}