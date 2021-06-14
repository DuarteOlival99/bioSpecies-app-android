package com.example.bioSpecies.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bioSpecies.data.entity.CadernetaItem
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.CadernetaViewModel
import kotlinx.android.synthetic.main.caderneta_expression.view.*
import kotlinx.android.synthetic.main.caderneta_list_expression.view.*

class CadernetaListaClassesAdapter(
        private var viewModel: CadernetaViewModel,
        private val context: Context?,
        private val layout: Int,
        private var listAnimaisClasses: MutableList<String>
) : RecyclerView.Adapter<CadernetaListaClassesAdapter.HistoryCanvasListViewHolder>(){

    class HistoryCanvasListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animalNome: TextView = view.GroupAText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryCanvasListViewHolder {

        return HistoryCanvasListViewHolder(
                LayoutInflater.from(context).inflate(layout, parent, false)
            )
    }

    override fun getItemCount(): Int {
      return listAnimaisClasses.size
    }

    override fun onBindViewHolder(holder: HistoryCanvasListViewHolder, position: Int) {
        holder.animalNome.text = listAnimaisClasses[position]

        val item = holder.itemView

        item.setOnClickListener {
            viewModel.setClassClicked(listAnimaisClasses[position])
            (context as AppCompatActivity).supportFragmentManager.let { NavigationManager.goToCadenetaLista(it) }
        }


    }

}