package com.spellchecker.arabickb.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.spellchecker.arabickb.database.*
import com.spellchecker.arabickb.databinding.ActivityTranslatedBinding
import com.spellchecker.arabickb.prefrences.SharedPrefres
import com.spellchecker.arabickb.utils.LangSelection
import java.util.*


class TranslatedActivity : AppCompatActivity() ,TextToSpeech.OnInitListener{
    private lateinit var binding:ActivityTranslatedBinding
    lateinit var bookmarkviewModel:BookmarkModel
    private var tts: TextToSpeech?=null
    var translate:String?=null
    var translated:String?=null
    var prefs: SharedPrefres?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bookmarkdao= Speaktranslatedb.getDatabase(this).BookmarkDao()
        val bookmarkRepoistry= BookmarkRepoistry(bookmarkdao)
        bookmarkviewModel= ViewModelProvider(this, BookmarkFactory(bookmarkRepoistry)).get(BookmarkModel::class.java)
        prefs= SharedPrefres(this)
        tts = TextToSpeech(this,this)

        binding.back.setOnClickListener {
            finish()
        }
        val intent = getIntent()
        translate = intent!!.getStringExtra("Translate")
        translated = intent!!.getStringExtra("Translated")

        binding.translate.setText(translate)
        binding.translated.setText(translated)
        binding.inptlang.text=LangSelection.Langnames[prefs!!.inputlangpos]
        binding.outputlang.text=LangSelection.Langnames[prefs!!.outputlangpos]
        binding.inputflag.setImageResource(LangSelection.Flagimg[prefs!!.inputlangpos])
        binding.outputflag.setImageResource(LangSelection.Flagimg[prefs!!.outputlangpos])
        binding.copy.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipdata = ClipData.newPlainText("text", translate)
            clipboardManager.setPrimaryClip(clipdata)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }
        binding.transltedCopy.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipdata = ClipData.newPlainText("text", translated)
            clipboardManager.setPrimaryClip(clipdata)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }
        binding.speak.setOnClickListener {
            speakouttranslate()
        }
        binding.translatedSpeak.setOnClickListener {
            speakouttranslated()
        }
        binding.bookmark.setOnClickListener{
            val bookmarkdata = BookmarkRecords(0,translate,translated,
                LangSelection.Langnames[prefs!!.inputlangpos],LangSelection.Langnames[prefs!!.outputlangpos])
            bookmarkviewModel.insertbookmarkRecord(bookmarkdata)
            Toast.makeText(this, "Added to Bookmark", Toast.LENGTH_SHORT).show()
        }
    }
    private fun speakouttranslate(){
        val text = translate
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
    private fun speakouttranslated(){
        val text = translated
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onInit(p0: Int) {
        if(p0==TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale("en"))
            if(result==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            { }else
            {
                binding.speak.isEnabled=true
                binding.translated.isEnabled=true
            }
        }else{
            Log.e("TTS","Initialized Failed")
        }
    }
    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    override fun onPause() {
        if (tts != null) {
            tts!!.stop()
        }
        super.onPause()
    }
}