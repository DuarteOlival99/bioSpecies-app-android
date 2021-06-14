package com.example.bioSpecies.ui.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.CapturaItem
import com.example.bioSpecies.data.entity.ChallengeItem
import com.example.bioSpecies.ui.viewmodels.viewmodels.ChallengesViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.caderneta_expression.view.*
import kotlinx.android.synthetic.main.capture_history_expression.view.*
import kotlinx.android.synthetic.main.challenges_atuais_expression.view.*

class ChallengesListAdapter (
    private var viewModel: ChallengesViewModel,
    private val context: Context,
    private val layout: Int,
    private var listChallenges: MutableList<ChallengeItem>
) : RecyclerView.Adapter<ChallengesListAdapter.ChallengesCanvasListViewHolder>(){

    class ChallengesCanvasListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val concluido: ImageView = view.desafios_complete_or_not
        val descricao: TextView = view.descricao_desafios_completados
        val dificuldadeImagem: ImageView = view.dificuldade_desafios_completados
        val desafioXP: TextView = view.desafio_xp

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengesListAdapter.ChallengesCanvasListViewHolder {
        return ChallengesCanvasListViewHolder(
            LayoutInflater.from(context).inflate(layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChallengesCanvasListViewHolder, position: Int) {
        holder.dificuldadeImagem.setImageResource(listChallenges[position].dificuldadeImagem)

        if (listChallenges[position].concluido){
            holder.concluido.setImageResource(R.drawable.ic_true)
        }else{
            holder.concluido.setImageResource(R.drawable.ic_close)
        }

        holder.descricao.text = listChallenges[position].descricao

        when (listChallenges[position].dificuldade) {
            0 -> {
                holder.dificuldadeImagem.setColorFilter(Color.GREEN)
                holder.desafioXP.text = "100 XP"
            }
            1 -> {
                holder.dificuldadeImagem.setColorFilter(Color.YELLOW)
                holder.desafioXP.text = "200 XP"
            }
            2 -> {
                holder.dificuldadeImagem.setColorFilter(Color.RED)
                holder.desafioXP.text = "300 XP"
            }
        }

        Log.i("ChallengesAdapter", listChallenges[position].descricao + position)
        val item = holder.itemView
    }

    override fun getItemCount(): Int {
        return listChallenges.size
    }



}