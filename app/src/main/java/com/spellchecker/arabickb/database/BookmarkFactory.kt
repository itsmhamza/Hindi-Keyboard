package com.spellchecker.arabickb.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookmarkFactory(private val bookmarkRepositry: BookmarkRepoistry):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookmarkModel(bookmarkRepositry) as T
    }
}