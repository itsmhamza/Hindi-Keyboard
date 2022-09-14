package com.spellchecker.arabickb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "speaktranslate")
data class Speaktranslation(@PrimaryKey(autoGenerate = true) var s_id:Int,
                            @ColumnInfo(name="inputword") var inputword:String?,
                            @ColumnInfo(name="outword") var outword:String?,
                            @ColumnInfo(name = "inputlang") var inputlang: Int,
                            @ColumnInfo(name = "outputlang") var outputlang: Int,
                            @ColumnInfo(name = "typeval") var typeval: Int)
