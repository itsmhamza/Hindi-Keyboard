package dev.patrickgold.florisboard.ime.suggesstions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.suggesstions.db.SuggesstionEntity
import kotlinx.android.synthetic.main.layout_suggesstion.view.*

class SuggestionsAdapter(
    val onclick: (String) -> Unit
) :
    RecyclerView.Adapter<SuggestionsAdapter.SuggestionsViewholder>() {

    var suggesstionList: MutableList<SuggesstionEntity>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SuggestionsViewholder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_suggesstion, parent, false)
    )

    override fun onBindViewHolder(holder: SuggestionsViewholder, position: Int) {
        suggesstionList?.get(position)?.word?.let { holder.bindData(it) }
    }

    override fun getItemCount(): Int = suggesstionList?.count() ?: 0

    fun setSuggestionList(suggestionList: MutableList<SuggesstionEntity>) {
        suggesstionList?.clear()
        suggesstionList = suggestionList
    }

    fun clearlist() {
        suggesstionList?.clear()
        notifyDataSetChanged()
    }

    inner class SuggestionsViewholder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(suggestion: String) {
            view.suggestionItem.text = suggestion
            view.suggestionItem.setOnClickListener { onclick.invoke(suggestion) }
        }
    }

    interface SuggesstionClick {
        fun onClick(suggestion: String)
    }
}