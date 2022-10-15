package com.spellchecker.arabickb.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.database.BookmarkRecords
import com.spellchecker.arabickb.database.HistoryRecords
import com.spellchecker.arabickb.utils.EMOJIS
import java.util.*
import kotlin.collections.ArrayList

class VoiceBookmarkAdopter(private val context: Context, val voicehistory: List<BookmarkRecords>) :
    RecyclerView.Adapter<VoiceBookmarkAdopter.MyViewHolder>() {


    inner class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var input: TextView
        var output: TextView

        init {
            input = parent.findViewById(R.id.historyinput) as TextView
            output = parent.findViewById(R.id.historyoutput) as TextView
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
        holder.output.setText(history.outwordbookmark)
    }

    override fun getItemCount(): Int {
        return voicehistory.size
    }
}