package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData

class BookmarkRepoistry(val bookmarkdao: BookmarkDao) {

    fun getAllHistoryRecords():LiveData<List<BookmarkRecords>>{
        return bookmarkdao.getBookmarkRecords()
    }
   suspend fun insertHistoryRecords(bookmarkdata:BookmarkRecords){
       bookmarkdao.inserBookmarkData(bookmarkdata)
    }
    fun deleteHistoryRecord(bookmarkdata: BookmarkRecords){
        bookmarkdao.deleteBookmarkRecord(bookmarkdata)
    }
}