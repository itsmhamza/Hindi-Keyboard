package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SpeakDao {
    @Query("SELECT * FROM speaktranslate ORDER BY s_id DESC")


    fun getAll(): LiveData<List<Speaktranslation>>

    @Insert
    fun  insertallhistory(vararg saveHistory: Speaktranslation)

    @Delete
    fun delete(dataitem: Speaktranslation)

    @Query("DELETE FROM speaktranslate")
    fun deleteAll()
}