package com.example.bioSpecies.ui.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.CadernetaItem
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.CadernetaViewModel
import kotlinx.android.synthetic.main.caderneta_expression.view.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class CadernetaListAnimal1Adapter(
    private var viewModel: CadernetaViewModel,
    private val context: Context,
    private val layout: Int,
    private var listAnimais: MutableList<Animal>
) : RecyclerView.Adapter<CadernetaListAnimal1Adapter.HistoryCanvasListViewHolder>(){

    class HistoryCanvasListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.animal_photo
        val animalNome: TextView = view.animal_name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryCanvasListViewHolder {

        return HistoryCanvasListViewHolder(
                LayoutInflater.from(context).inflate(layout, parent, false)
            )
    }

    override fun getItemCount(): Int {
      return listAnimais.size
    }

    override fun onBindViewHolder(holder: HistoryCanvasListViewHolder, position: Int) {
        if (listAnimais[position].descoberto){
            Glide.with(holder.image).load(listAnimais[position].fotoMediumUrl).into(holder.itemView.animal_photo)
            holder.animalNome.text = listAnimais[position].nomeTradicional

            val item = holder.itemView

            item.setOnClickListener {
                viewModel.setAnimalSelected(listAnimais[position])
                (context as AppCompatActivity).supportFragmentManager.let { NavigationManager.goToPerfilEspecies(it) }
            }

        } else {
            holder.itemView.animal_photo.setImageResource(R.drawable.ic__no_photography)
            holder.animalNome.text = context.getString(R.string.animal_no_name)
        }
    }

}