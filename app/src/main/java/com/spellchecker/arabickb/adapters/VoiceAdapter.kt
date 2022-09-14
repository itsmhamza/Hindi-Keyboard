package com.spellchecker.arabickb.adapters

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.database.SharedViewModel
import com.spellchecker.arabickb.database.Speaktranslation
import com.spellchecker.arabickb.databinding.ItemSpTranslateBinding
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.LangSelection.setClipboard
import com.spellchecker.arabickb.utils.SpeakConstants
import com.spellchecker.arabickb.utils.SpeechText
import java.util.*

class VoiceAdapter(var listvoice:List<Speaktranslation>, var mainviewmdel: SharedViewModel):
    RecyclerView.Adapter<VoiceAdapter.VoiceHolder>(), SpeechText.OnCompleteVoice {

    var constants: SpeakConstants?=null
    var textToSpeech: TextToSpeech? = null
    private var mcontxt: Context? =null

    inner class VoiceHolder(val binding: ItemSpTranslateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceHolder {
        val binding = ItemSpTranslateBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        mcontxt=parent.context

        return VoiceHolder(binding)

    }

    override fun onBindViewHolder(holder: VoiceHolder, position: Int) {
with(holder){
with(listvoice[position]){
    if (typeval==1){
        binding.viewad.setBackgroundResource(R.drawable.bg_input)
    }
    else{
        binding.viewad.setBackgroundResource(R.drawable.bg_output)
        binding.leftmicivcopy.setColorFilter(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.leftmicivdelete.setColorFilter(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.leftmicivspeak.setColorFilter(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.keyboardRoot.setBackgroundColor(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.inputlangaugetext.setTextColor(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.inputtext.setTextColor(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.outputlangtxt.setTextColor(ContextCompat.getColor(mcontxt!!,R.color.white))
        binding.outtxt.setTextColor(ContextCompat.getColor(mcontxt!!,R.color.white))
    }
    binding.inputtext.text = inputword
    binding.inputlangaugetext.text = LangSelection.Langnames[inputlang]
    binding.outputlangtxt.text = LangSelection.Langnames[outputlang]
    binding.outtxt.text = outword
    binding.leftmicflaginput.setImageResource(LangSelection.Flagimg[inputlang])
    binding.flagoutput.setImageResource(LangSelection.Flagimg[outputlang])
    binding.leftmicivcopy.setOnClickListener {
        setClipboard(mcontxt!!, binding.outtxt.text.toString())
    }
    binding.leftmicivdelete.setOnClickListener {


        deleteEntry(listvoice[position])

    }

    binding.leftmicivspeak.setOnClickListener {
        speakOut(
            binding.outtxt.text.toString(),
            LangSelection.Langcode[outputlang], binding.outtxt.text.toString()
        )
    }
}
}
    }
    fun deleteEntry(voicelist:Speaktranslation) {
        mainviewmdel.deleteVoiceTranslation(voicelist)
        notifyDataSetChanged()
    }
    fun speakOut(speaktext: String,outputlanguage:String,inputdata:String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            translateData(speaktext,outputlanguage,inputdata)
        } else {
            textToSpeech(speaktext,outputlanguage)
        }
    }
    private fun translateData(data: String,outputlangcode:String,inputdata:String) {
        constants= mcontxt?.let { SpeakConstants(it) }
        constants?.forSpeakSaveInitialization(
            outputlangcode
        )

        constants?.forSpeakAndSaveSpeak(
            data,
            outputlangcode,
            inputdata,
            data,
            this,
            false
        )
    }
    private fun textToSpeech(wordToSpeak: String,outputcode:String) {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        textToSpeech?.language = Locale(outputcode)
        textToSpeech?.speak(wordToSpeak, TextToSpeech.QUEUE_FLUSH, map)
    }
    override fun getItemCount(): Int {
return listvoice.size
    }

    override fun onComplete() {

    }

    override fun onFail() {

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}