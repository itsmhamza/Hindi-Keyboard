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
import com.spellchecker.arabickb.utils.EMOJIS
import java.util.*
import kotlin.collections.ArrayList

class emojiAdopter(private val context: Context, val imagesList: ArrayList<String>,private val listener:onItemClickListener) :
    RecyclerView.Adapter<emojiAdopter.MyViewHolder>() {

    private lateinit var mlistener: onItemClickListener
    var emojilist = ArrayList<String>()

    init {
        emojilist = imagesList
        mlistener = listener
    }

    interface onItemClickListener {
        fun onItemClick(Item: String)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistener = listener
    }

    inner class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var emoji: TextView

        init {
            emoji = parent.findViewById(R.id.emoji) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.emoji_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val emoji = imagesList[position]
        holder.emoji.setText(EmojiCompat.get().process(Html.fromHtml(emoji)))
        holder.itemView.setOnClickListener {
            mlistener.onItemClick(emoji)
        }

    }

    override fun getItemCount(): Int {
        return imagesList.size
    }
}