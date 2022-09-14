package dev.patrickgold.florisboard.ime.mic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dev.patrickgold.florisboard.ime.core.PrefHelper
import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.translation.Translation
import dev.patrickgold.florisboard.util.Constants
import dev.patrickgold.florisboard.util.getamilSubtype
import dev.patrickgold.florisboard.util.isInternetOn
import dev.patrickgold.florisboard.util.showAsToast
import kotlinx.coroutines.*

abstract class SpeechRecognition(val context: Context, val prefs: PrefHelper) :
    CoroutineScope by MainScope(),
    RecognitionListener {

    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    private var isInternetOn: Boolean = false


    private fun getSpeechRecognizer(): SpeechRecognizer? {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
            it.setRecognitionListener(this)
            mSpeechRecognizer = it
        }
        return mSpeechRecognizer
    }


    private fun getRecongnizerIntent() =
        speechIntent ?: Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra("android.speech.extra.LANGUAGE", "en")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }.also {
            speechIntent = it
        }


    fun listenSpeech(beforeListen: () -> Unit) {

        val lang =
            if (Subtype.DEFAULT == getamilSubtype() || Subtype.DEFAULT == getamilSubtype(true))
                Constants.KEYBOARD_DEFAULT_MIC_LANG
            else
                when {
                    prefs.keyboard.transletration == "on" &&
                            prefs.keyboard.texttranslator == "off" -> Constants.KEYBOARD_DEFAULT_MIC_LANG

                    else -> Constants.KEYBOARD_MIC_LANG
                }

        if (context.isInternetOn()) {
            beforeListen.invoke()
            getSpeechRecognizer()?.startListening(getRecongnizerIntent().apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
            })

        } else
            "No Internet".showAsToast(context)

    }

    fun stopListen() {
        mSpeechRecognizer?.stopListening()
        mSpeechRecognizer = null
    }

    fun getResultFromBundle(
        results: Bundle?,
        isTranslationOn: Boolean = false,
        inputLangCode: String = "en",
        outputLangCode: String = "ur",
        onResult: (String) -> Unit
    ) {
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
        return if (isTranslationOn) {
            val translation = Translation(context)
            var result = ""
            val j = launch(Dispatchers.IO) {
                result =
                    translation.runTranslationAsync(text, outputLangCode, inputLangCode).await()
                withContext(Dispatchers.Main) {
                    onResult.invoke(result)
                }
            }
        } else
            onResult.invoke(text)

    }

    override fun onBeginningOfSpeech() {

    }

    override fun onReadyForSpeech(params: Bundle?) {

    }


    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }


    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

//    override fun onResults(results: Bundle?) {
//        val resultText =
//            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
//
//    }

}