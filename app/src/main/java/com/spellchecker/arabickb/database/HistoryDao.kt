package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {

@Query("SELECT * FROM historydata ORDER BY h_id DESC")

fun gethistooryRecords():LiveData<List<HistoryRecords>>

@Insert
fun inserHistoryData(vararg historydata:HistoryRecords)

@Delete

fun deetehistoryRecord(historyitem:HistoryRecords)
}