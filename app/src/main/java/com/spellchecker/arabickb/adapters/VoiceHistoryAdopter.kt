package com.spellchecker.arabickb.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.database.HistoryRecords
import com.spellchecker.arabickb.ui.HistoryActivity
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.LangSelection.Lang
import com.spellchecker.arabickb.utils.LangSelection.Langnames

class VoiceHistoryAdopter(private val context: Context, val voicehistory: List<HistoryRecords>) :
    RecyclerView.Adapter<VoiceHistoryAdopter.MyViewHolder>() {


    inner class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var input: TextView
        var click: CardView

        init {
            input = parent.findViewById(R.id.historyinput) as TextView
            click = parent.findViewById(R.id.click) as CardView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.voicehistory_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = voicehistory[position]
        holder.input.setText(history.inputwordhistory)
        holder.click.setOnClickListener {
            val intent = Intent(context,HistoryActivity::class.java)
            intent.putExtra("translate",history.inputwordhistory)
            intent.putExtra("translated",history.outwordhistory)
            intent.putExtra("input",history.inputlanghistory)
            intent.putExtra("output",history.outputlanghistory)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return voicehistory.size
    }
}