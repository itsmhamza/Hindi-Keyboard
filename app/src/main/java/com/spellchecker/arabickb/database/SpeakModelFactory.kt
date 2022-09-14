package com.spellchecker.arabickb.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SpeakModelFactory (private val voicerepositry:SpeakRepositry): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SharedViewModel(voicerepositry) as T
    }
}