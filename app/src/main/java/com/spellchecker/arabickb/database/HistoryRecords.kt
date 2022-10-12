package com.spellchecker.arabickb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historydata")
data class HistoryRecords(@PrimaryKey(autoGenerate = true) var h_id:Int,
                          @ColumnInfo(name="inputword") var inputwordhistory:String?,
                          @ColumnInfo(name="outputword") var outwordhistory:String?,
                          @ColumnInfo(name = "inputlang") var inputlanghistory: String?,
                          @ColumnInfo(name = "outputlang") var outputlanghistory: String?,)
