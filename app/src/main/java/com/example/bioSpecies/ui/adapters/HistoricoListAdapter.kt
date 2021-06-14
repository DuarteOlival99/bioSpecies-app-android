package com.example.bioSpecies.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.CapturaItem
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.viewmodels.CadernetaViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.capture_history_expression.view.*

class HistoricoListAdapter(
        private var viewModel: ProfileViewModel,
        private val context: Context,
        private val layout: Int,
        private var listHistorico: List<Photos>
) : RecyclerView.Adapter<HistoricoListAdapter.HistoryCanvasListViewHolder>(){

    class HistoryCanvasListViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val captureImage: ImageView = view.historico_foto_expression
        val captureName: TextView = view.historico_nome_expression
        val captureClassification: TextView = view.historico_Classificacao_expression
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoListAdapter.HistoryCanvasListViewHolder {

        return HistoryCanvasListViewHolder(
            LayoutInflater.from(context).inflate(layout, parent, false)
        )

    }

    override fun onBindViewHolder(holder: HistoryCanvasListViewHolder, position: Int) {
        holder.captureImage.setImageBitmap(listHistorico[position].image)
        holder.captureName.text = listHistorico[position].animalName
        when (listHistorico[position].resultado){
            -1 -> {
                holder.captureClassification.text = context.getString(R.string.pendente)
            }
            0 -> {
                holder.captureClassification.text = context.getString(R.string.recusada)
            }
            1 -> {
                holder.captureClassification.text = context.getString(R.string.aceite)
            }
        }
        val item = holder.itemView

    }

    override fun getItemCount(): Int {
        return listHistorico.size
    }


}

