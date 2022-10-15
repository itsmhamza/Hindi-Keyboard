package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkModel(private val bookmarkRepoistry: BookmarkRepoistry):ViewModel() {

    fun getAllbookmarkData():LiveData<List<BookmarkRecords>>
    {
        return bookmarkRepoistry.getAllHistoryRecords()
    }

    fun insertbookmarkRecord(bookmarkRecords: BookmarkRecords)
    {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepoistry.insertHistoryRecords(bookmarkRecords)
        }

    }

    fun deletebookmarkRecord(bookmarkRecords: BookmarkRecords){
        bookmarkRepoistry.deleteHistoryRecord(bookmarkRecords)
    }
}