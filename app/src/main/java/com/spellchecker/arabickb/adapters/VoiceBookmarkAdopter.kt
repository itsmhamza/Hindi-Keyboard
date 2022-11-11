package com.spellchecker.arabickb.adapters

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.database.BookmarkRecords
import com.spellchecker.arabickb.database.HistoryRecords
import com.spellchecker.arabickb.ui.HistoryActivity
import com.spellchecker.arabickb.utils.EMOJIS
import com.spellchecker.arabickb.utils.LangSelection
import java.util.*
import kotlin.collections.ArrayList

class VoiceBookmarkAdopter(private val context: Context, val voicehistory: List<BookmarkRecords>) :
    RecyclerView.Adapter<VoiceBookmarkAdopter.MyViewHolder>() {


    inner class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var input: TextView
        var click:CardView

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
        holder.input.setText(history.inputwordbookmark)
        holder.click.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            intent.putExtra("translate",history.inputwordbookmark)
            intent.putExtra("translated",history.outwordbookmark)
            intent.putExtra("input",history.inputlanghbookmark)
            intent.putExtra("output",history.outputlanghbookmark)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return voicehistory.size
    }

}