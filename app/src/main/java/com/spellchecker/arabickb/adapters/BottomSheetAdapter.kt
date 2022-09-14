package com.spellchecker.arabickb.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.utils.Languages
import java.util.*
import kotlin.collections.ArrayList

class BottomSheetAdapter(private val language_list: ArrayList<Languages>, private val listener: onItemClickListener) :
    RecyclerView.Adapter<BottomSheetAdapter.MyViewHolder>(), Filterable {

    private lateinit var mlistener: onItemClickListener
    var copycountrieslist = ArrayList<Languages>()

    init {
        copycountrieslist = language_list
        mlistener = listener
    }

    interface onItemClickListener {
        fun onItemClick(Item: Languages)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_lang_selection,
            parent,
            false
        )
        return MyViewHolder(itemView, mlistener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = copycountrieslist[position]
        //holder.titleImage.setImageResource(currentItem.image)
        holder.tvHeading.text = currentItem.name
        holder.itemView.setOnClickListener {
            mlistener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return copycountrieslist.size
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistener = listener
    }

    class MyViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var titleImage: ImageView = itemView.findViewById(R.id.ivflag)
        var tvHeading: TextView = itemView.findViewById(R.id.langname)
    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    copycountrieslist = language_list
                } else {
                    val resultList = ArrayList<Languages>()

                    for (row in language_list) {
                        if (row.name.toLowerCase(Locale.ROOT)
                                .startsWith(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    copycountrieslist = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = copycountrieslist
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                copycountrieslist = p1?.values as ArrayList<Languages>
                notifyDataSetChanged()
            }
        }
    }
}