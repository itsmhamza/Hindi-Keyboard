package com.spellchecker.arabickb.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryFactory(private val historyRepoistry: HistoryRepoistry):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HistoryModel(historyRepoistry) as T
    }
}