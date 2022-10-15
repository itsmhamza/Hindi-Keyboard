package com.spellchecker.arabickb.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel (private val voicerepositry:SpeakRepositry): ViewModel() {


    fun getvoicetranslation(): LiveData<List<Speaktranslation>> {
        return voicerepositry.gettranslations()
    }
    fun insertVoiceTranslation(voicetrans:Speaktranslation){
        viewModelScope.launch(Dispatchers.IO) {
            voicerepositry.inserttrnaslation(voicetrans)
        }

    }

    fun deleteVoiceTranslation(voicetrans:Speaktranslation){
        viewModelScope.launch(Dispatchers.IO) {
            voicerepositry.deleteData(voicetrans)
        }

    }
}