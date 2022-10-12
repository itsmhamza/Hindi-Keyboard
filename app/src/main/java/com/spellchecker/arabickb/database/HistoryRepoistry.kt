package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData

class HistoryRepoistry(val historyDao: HistoryDao) {

    fun getAllHistoryRecords():LiveData<List<HistoryRecords>>{
        return historyDao.gethistooryRecords()
    }
   suspend fun insertHistoryRecords(historydata:HistoryRecords){
         historyDao.inserHistoryData(historydata)
    }
    fun deleteHistoryRecord(historydata: HistoryRecords){
        historyDao.deetehistoryRecord(historydata)
    }
}