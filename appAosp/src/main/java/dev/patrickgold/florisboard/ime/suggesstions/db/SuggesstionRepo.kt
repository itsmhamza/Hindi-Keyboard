package dev.patrickgold.florisboard.ime.suggesstions.db

import android.content.Context
import kotlinx.coroutines.*

class SuggesstionRepo(val context: Context) {

    companion object {
        private var instance: SuggesstionRepo? = null

        fun getInstance(context: Context) =
            instance ?: SuggesstionRepo(context).also { instance = it }

    }

    private var suggesstionDao: SuggesstionDao =
        SuggesstionsDatabase.getInstance(context).getSuggesstionDao()


    fun insert(suggesstionEntity: SuggesstionEntity) {
        suggesstionDao.insertSuggesstion(suggesstionEntity)
    }

    fun retriveAllSuggesstions(list: (List<SuggesstionEntity>) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            val suggestionFetchJob = async {
                suggesstionDao.retrieveAll()
            }
            val suggestionList = suggestionFetchJob.await()

            withContext(Dispatchers.Main) {
                list.invoke(suggestionList)
            }
        }

    }

    fun retrieveFilterSuggesstions(word: String) = suggesstionDao.filterSuggesstions("$word%")

}