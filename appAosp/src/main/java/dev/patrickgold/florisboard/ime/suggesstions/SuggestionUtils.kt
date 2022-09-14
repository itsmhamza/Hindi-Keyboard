package dev.patrickgold.florisboard.ime.suggesstions

import android.content.Context
import dev.patrickgold.florisboard.ime.suggesstions.db.SuggesstionEntity
import dev.patrickgold.florisboard.ime.suggesstions.db.SuggesstionRepo

object SuggestionUtils {

    private var suggestionList: List<SuggesstionEntity>? = null

    fun getSuggesstionList() =
        suggestionList

    fun setupSuggestions(context: Context) {
        val repo = SuggesstionRepo(context)

//        val list = repo.retrieveFilterSuggesstions("debug")
//        if (list.isNotEmpty())
//            list.forEach {
//                showToast(it.word.toString())
//            }
//
//        repo.insert(SuggesstionEntity(System.currentTimeMillis().toInt(),"debugging"))

        repo.retriveAllSuggesstions {
            suggestionList = it
        }
    }
}