package com.spellchecker.arabickb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.databinding.ItemLangSelectionBinding
import com.spellchecker.arabickb.prefrences.SharedPrefres
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.Languages
import java.util.*


class LanguageSelectionAdapter(private val language_list: ArrayList<Languages>, private val listener: LanguageSelectionAdapter.onItemClickListener):RecyclerView.Adapter<LanguageSelectionAdapter.LangViewholder>(),Filterable {
    var prefs:SharedPrefres?=null
var getUserModelListFiltered:List<String>?=null
var userModelList:List<Languages>?=null

    private lateinit var mlistener:onItemClickListener
    var copycountrieslist = ArrayList<Languages>()
    var countrieslists = ArrayList<Languages>()
    init {
        copycountrieslist = language_list
        mlistener = listener
    }
    interface onItemClickListener {
        fun onItemClick(Item: Languages)
    }
   inner class LangViewholder(val langbinding:ItemLangSelectionBinding):RecyclerView.ViewHolder(langbinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangViewholder {
        val langbinding=ItemLangSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        prefs= SharedPrefres(parent.context)
        return LangViewholder(langbinding)
    }

    override fun onBindViewHolder(holder: LangViewholder, position: Int) {
with(holder){

    with(copycountrieslist[position]) {

       langbinding.ivflag.setImageResource(copycountrieslist[position].image)
      // langbinding.ivflag.setImageResource(copycountrieslist.[position])
       langbinding.langname.text = copycountrieslist[position].name


        langbinding.selectedimg.visibility = View.GONE
            if (prefs!!.lanselectionpos == 1) {
                //if (prefs!!.inputlangpos==getLangPos(copycountrieslist[position])){

                       if(copycountrieslist[position].name.equals(prefs!!.lannameposition)){
                    langbinding.selectedimg.visibility = View.VISIBLE
                }



            }
        if (prefs!!.lanselectionpos == 2) {
                if(copycountrieslist[position].name.equals(prefs!!.outputlanposition)){
                    langbinding.selectedimg.visibility = View.VISIBLE
                }




        }

    }




}
        holder.itemView.setOnClickListener {
            if (prefs!!.lanselectionpos==1){

              //  prefs!!.inputlangpos=getLangPos(copycountrieslist[position])
                prefs!!.lannameposition=copycountrieslist[position].name
            }
            else{
                prefs!!.outputlanposition=copycountrieslist[position].name
              //  prefs!!.outputlangpos=getLangPos(copycountrieslist[position])

            }
            //prefs!!.inputlangposition=copycountrieslist[position].name
            mlistener.onItemClick(copycountrieslist[position])
            notifyDataSetChanged()
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int=copycountrieslist.size

    /* access modifiers changed from: 0000 */

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
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                copycountrieslist = p1?.values as ArrayList<Languages>
                notifyDataSetChanged()
            }
        }
    }
    private fun getLangPos(lanpos: Languages): Int {
        return countrieslists.indexOf(lanpos)
    }
}