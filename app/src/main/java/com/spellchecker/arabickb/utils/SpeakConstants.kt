package com.spellchecker.arabickb.utils

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class SpeakConstants (private val contxt: Context) {

    lateinit var speakForSave: SpeechText

    companion object {

        var pitch = 1f
        var speed = .7f
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun forSpeakAndSaveSpeak(
        textForSpeak: String,
        outputCode: String,
        inputName: String,
        outputName: String,
        lister: SpeechText.OnCompleteVoice,
        flag: Boolean
    ): String {

        Log.e("TAG", "forSpeakAndSaveSpeak: $flag")
        return speakForSave.onStart(
            textForSpeak,
            speed,
            pitch, outputCode,
            inputName, outputName, flag,true, lister
        )

    }
    fun forSpeakAndSave(): SpeechText {
        return speakForSave
    }
    fun forSpeakSaveInitialization(outputCode: String) {
        speakForSave =
            SpeechText(
                contxt,
                outputCode
            )
    }
}