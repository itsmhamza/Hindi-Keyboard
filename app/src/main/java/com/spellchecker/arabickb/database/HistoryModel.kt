package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryModel(private val historyRepoistry: HistoryRepoistry):ViewModel() {

    fun getAllHistryData():LiveData<List<HistoryRecords>>
    {
        return historyRepoistry.getAllHistoryRecords()
    }

    fun insertHistoryRecord(historyRecords: HistoryRecords)
    {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepoistry.insertHistoryRecords(historyRecords)
        }

    }

    fun deleteHistoryRecord(historyRecords: HistoryRecords){
        historyRepoistry.deleteHistoryRecord(historyRecords)
    }
}