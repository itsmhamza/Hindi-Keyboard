package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData

class SpeakRepositry (val voiceDao:SpeakDao){

    fun gettranslations(): LiveData<List<Speaktranslation>> {
        return voiceDao.getAll()
    }
    suspend fun inserttrnaslation(voicedata:Speaktranslation){
        voiceDao.insertallhistory(voicedata)
    }
    suspend fun deleteData(voicedata:Speaktranslation){
        voiceDao.delete(voicedata)
    }
}