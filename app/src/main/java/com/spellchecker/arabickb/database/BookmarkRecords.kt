package com.spellchecker.arabickb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarkdata")
data class BookmarkRecords(@PrimaryKey(autoGenerate = true) var b_id:Int,
                           @ColumnInfo(name="inputword") var inputwordbookmark:String?,
                           @ColumnInfo(name="outputword") var outwordbookmark:String?,
                           @ColumnInfo(name = "inputlang") var inputlanghbookmark: String?,
                           @ColumnInfo(name = "outputlang") var outputlanghbookmark: String?,)
