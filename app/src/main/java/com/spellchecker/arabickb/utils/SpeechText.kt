package com.spellchecker.arabickb.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SpeechText (context: Context, language: String) : TextToSpeech.OnInitListener {

    private var str: String? = null
    private val mTts: TextToSpeech?
    private val context: Context
    private var language: String
    private var fileSave = false
    private var speak_flag = false
    private var inputName = ""
    private var outputName = ""
    private var fileTTS: File? = null
    private var oneTimeToast = true
    private var onCompleteVoice: OnCompleteVoice? = null
    fun tts(): TextToSpeech? {
        return mTts
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun onStart(
        str: String?, speech: Float,
        pitch: Float, language: String, inputName: String,
        outputName: String, fileSave: Boolean, speak_flag: Boolean,
        onCompleteVoice: OnCompleteVoice?
    ): String {
        this.str = str
        this.language = language
        this.fileSave = fileSave
        this.speak_flag = speak_flag
        this.outputName = outputName
        this.inputName = inputName
        this.onCompleteVoice = onCompleteVoice
        if (speak_flag) {
            mTts!!.setSpeechRate(speech)
            mTts.setPitch(pitch)
        }
        return sayHello(str)
    }

    fun onStop() {
        mTts?.stop()
    }
    fun filePath(context: Context): File {
        return File(context.getExternalFilesDir("").toString() + "/audioFiles/")
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onInit(status: Int) {
        Log.v(TAG, "onInit")
        if (status == TextToSpeech.SUCCESS) {
            mTts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(s: String) {
                    Log.e("start", "yes")
                }

                override fun onDone(s: String) {
                    if (fileTTS!!.exists()) {
                        if (oneTimeToast) {
                            Log.d(TAG, "successfully created fileTTS")
                            if (onCompleteVoice != null) onCompleteVoice!!.onComplete()
                            fileTTS?.let { Log.e("this_", it.getAbsolutePath()) }
                            oneTimeToast = false

//                            Intent savedFiles = new Intent(context, SavedFiles.class);
//                            savedFiles.putExtra(Constants.showAdd, true);
//                            context.startActivity(savedFiles);
                        }
                    } else {
                        Log.d(TAG, "failed while creating fileTTS")
                        if (onCompleteVoice != null) onCompleteVoice!!.onFail()
                    }
                }

                override fun onError(s: String) {
                    Log.e("error", "error")
                    if (onCompleteVoice != null) onCompleteVoice!!.onFail()
                }
            })
            val result = mTts.setLanguage(getLanguage(language))
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Log.v(TAG, "Language is not available.")
            } else {
                Log.v(TAG, "Running.")
                sayHello(str)
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.")
        }
        if (status == TextToSpeech.STOPPED) {
            Log.v(TAG, "Stopped.")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun sayHello(str: String?): String {
        val outputPath: File =filePath(context)
        Log.e(TAG, "sayHello: $str")
        if (!outputPath.exists()) {
            val x: Boolean = outputPath.mkdirs()
            Log.d("pathFile", "result $x")
        }
        val c: Calendar = Calendar.getInstance()
        System.out.println("Current time => " + c.getTime())
        val df = SimpleDateFormat("dd-MMM-yyyy HH.mm.ss", Locale.getDefault())
        val formattedDate: String = df.format(c.getTime())
        Log.e("this_", formattedDate)
        val fileName = "/" + inputName + "_" + outputName + ";" + "File" + ".wav"
        val output: String = File(outputPath, fileName).getAbsolutePath()
        if (speak_flag) {
            mTts!!.language = getLanguage(language)
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId")
            mTts.speak(
                str,
                TextToSpeech.QUEUE_FLUSH,
                params,
                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
            )
        }
        Log.e(TAG, "sayHello: output_file$output")
        Log.e(TAG, "sayHello:fileSave  $fileSave")
        Log.e(TAG, "sayHello:speak_flag  $speak_flag")
        //        if (fileSave)
//            return  saveFile(str, output);
//        else return str;
        return saveFile(str, output)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun saveFile(str: String?, output: String): String {
        Log.e(TAG, "saveFile: str$str")
        Log.e(TAG, "saveFile: output$output")
        fileTTS = File(output)
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId")
        if (mTts != null) {
            mTts.synthesizeToFile(str, params, fileTTS, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
        }
        return output
    }

    fun destroy() {
        mTts?.shutdown()
    }

    private fun getLanguage(language: String): Locale {
        when (language) {
            "English" -> return Locale.ENGLISH
            "French" -> return Locale.FRENCH
            "German" -> return Locale.GERMAN
            "Chinese" -> return Locale.CHINESE
            "Italian" -> return Locale.ITALIAN
            "Japanese" -> return Locale.JAPANESE
            "Korean" -> return Locale.KOREAN
            "Simplified Chinese" -> return Locale.SIMPLIFIED_CHINESE
            "Traditional Chinese" -> return Locale.TRADITIONAL_CHINESE
        }
        return Locale.ENGLISH
    }

    interface OnCompleteVoice {
        fun onComplete()
        fun onFail()
    }

    companion object {
        private const val TAG = "TTSService"
    }

    init {
        mTts = TextToSpeech(
            context,
            this // OnInitListener
        )
        this.context = context
        this.language = language
    }
}