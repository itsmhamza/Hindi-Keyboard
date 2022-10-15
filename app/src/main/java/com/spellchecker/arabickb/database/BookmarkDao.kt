package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {

@Query("SELECT * FROM bookmarkdata ORDER BY b_id DESC")

fun getBookmarkRecords():LiveData<List<BookmarkRecords>>

@Insert
fun inserBookmarkData(vararg bookmarkdata:BookmarkRecords)

@Delete

fun deleteBookmarkRecord(bookmarkitem:BookmarkRecords)
}