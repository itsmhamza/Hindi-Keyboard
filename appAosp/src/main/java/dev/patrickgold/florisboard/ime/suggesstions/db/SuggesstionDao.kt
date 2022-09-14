package dev.patrickgold.florisboard.ime.suggesstions.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface SuggesstionDao {

    @Insert
    fun insertSuggesstion(suggesstionEntity: SuggesstionEntity)

    @Query("SELECT * FROM suggesstion_table")
    fun retrieveAll(): List<SuggesstionEntity>


    @Query("SELECT * FROM suggesstion_table WHERE word LIKE :word ")
    fun filterSuggesstions(word: String): MutableList<SuggesstionEntity>
}