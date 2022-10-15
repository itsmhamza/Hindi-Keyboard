package com.spellchecker.arabickb.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.appinterfaces.onItemClickListener
import com.spellchecker.arabickb.database.*
import com.spellchecker.arabickb.databinding.FragVoiceTranslateBinding
import com.spellchecker.arabickb.dialogs.LangSelectionDialog
import com.spellchecker.arabickb.prefrences.SharedPrefres
import com.spellchecker.arabickb.ui.TranslatedActivity
import com.spellchecker.arabickb.ui.VoiceTranslateBookmarkActivity
import com.spellchecker.arabickb.ui.VoiceTranslateHistoryActivity
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.Languages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException


class VoiceTranslateFragment:Fragment(),View.OnClickListener,onItemClickListener {

    lateinit var fragvoicebinding:FragVoiceTranslateBinding
    private var progressDialog: ProgressDialog? = null
    private val REQUEST_CODE = 100
    var prefs:SharedPrefres?=null
    var callback: onItemClickListener?=null
    lateinit var historyviewModel:HistoryModel

    companion object {

        fun newInstance() =
            VoiceTranslateFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragvoicebinding= FragVoiceTranslateBinding.inflate(inflater,container,false)

        return fragvoicebinding.root
    }
   fun uiViews(){
       callback=this
       // Inflate the layout for this fragment
       val histrydao=Speaktranslatedb.getDatabase(requireActivity()).HistoryDao()
       val historyRepoistry=HistoryRepoistry(histrydao)
       historyviewModel=ViewModelProvider(this,HistoryFactory(historyRepoistry)).get(HistoryModel::class.java)
       prefs= SharedPrefres(requireContext())
       fragvoicebinding.inputlangview.setOnClickListener(this)
       fragvoicebinding.outputputlangview.setOnClickListener(this)
       fragvoicebinding.translate.setOnClickListener(this)
       fragvoicebinding.copy.setOnClickListener(this)
       fragvoicebinding.del.setOnClickListener(this)
       fragvoicebinding.translate.setOnClickListener(this)
       fragvoicebinding.history.setOnClickListener(this)
       fragvoicebinding.bookmark.setOnClickListener(this)
       fragvoicebinding.inputlanguage.text= LangSelection.Langnames[prefs!!.inputlangpos]
       fragvoicebinding.inputlanguageimage.setImageResource(LangSelection.Flagimg[prefs!!.inputlangpos])
       fragvoicebinding.outputlanguage.text= LangSelection.Langnames[prefs!!.outputlangpos]
       fragvoicebinding.outputlanguageimage.setImageResource(LangSelection.Flagimg[prefs!!.outputlangpos])
   }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiViews()
    }

    private fun translate(word: String) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setTitle("Translating Your Text")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
        Ion.with(activity)
            .load("https://translate.googleapis.com/translate_a/single")
            .setHeader("User-Agent", "Mozilla/5.0")
            .setBodyParameter("client", "gtx")
            .setBodyParameter(
                "sl",
                LangSelection.Langcode[prefs!!.inputlangpos]

            )
            .setBodyParameter(
                "tl",
                LangSelection.Langcode[prefs!!.outputlangpos]
            )
            .setBodyParameter("dt", "t")
            // .setBodyParameter("q", URLEncoder.encode(word.replace("+", " "), "UTF-8"))
            .setBodyParameter("q", word.replace("\n", " "))
            // .setBodyParameter("q",  re.replace(word,""))
            .setBodyParameter("ie", "UTF-8")
            .setBodyParameter("oe", "UTF-8")
            .asString()
            .setCallback(object : FutureCallback<String?> {
                override fun onCompleted(e: Exception?, result: String?) {
                    if (e != null) {
                        progressDialog!!.dismiss()
                        Toast.makeText(
                            activity,
                            "Please check your internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    GlobalScope.launch(Dispatchers.Main) {
                        parseResult(result)
                    }
                }
            })
    }

    fun parseResult(inputJson: String?): String {

        val data: String?
        var data1: String? = null
        var data2 = ""
        try {
            val jsonArray = JSONArray(inputJson)
            val jsonArray2: JSONArray = jsonArray.get(0) as JSONArray
            data = ""
            for (i in 0 until jsonArray2.length()) {
                val jsonArray3: JSONArray = jsonArray2.get(i) as JSONArray
                data1 = data + jsonArray3.get(0).toString()
                data2 = data + jsonArray3.get(1).toString()
            }
            progressDialog!!.dismiss()
        } catch (e: JSONException) {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            progressDialog!!.dismiss()
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            progressDialog!!.dismiss()
        }
        data1?.let {
            val historydata = HistoryRecords(0,
                fragvoicebinding.texttranslate.text.toString(),
                it,
                LangSelection.Langnames[prefs!!.inputlangpos],
                LangSelection.Langnames[prefs!!.outputlangpos],
            )
            historyviewModel.insertHistoryRecord(historydata)
            Log.d("HISTORYDATA",historyviewModel.getAllHistryData().toString())
            //  binding.translated.text = it
            val intent = Intent(activity,TranslatedActivity::class.java)
            intent.putExtra("Translated",it)
            intent.putExtra("Translate",fragvoicebinding.texttranslate.text.toString())
            startActivity(intent)
        }
        return "$data1 $data2"
    }

    private fun speak_() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, fragvoicebinding.texttranslate.text)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak and Translate")
        try {
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(activity, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    fragvoicebinding.texttranslate.setText(result?.get(0).toString())
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.inputlangview->{
                prefs!!.lanselectionpos=1
                requireActivity().supportFragmentManager.let {
                    LangSelectionDialog(callback).apply {
                        show(it, "inputspinner")
                    }
                }
            }
            R.id.outputputlangview->{
                prefs!!.lanselectionpos=2
                requireActivity().supportFragmentManager.let {
                    LangSelectionDialog(callback).apply {
                        show(it, "inputspinner")
                    }
                }
            }
            R.id.translate->
            {
                if (fragvoicebinding.texttranslate.text.isNotEmpty()) {
                    translate(fragvoicebinding.texttranslate.text.toString())
                }else{
                    Toast.makeText(activity, "Empty Text Field", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.copy->
            {
                if(fragvoicebinding.texttranslate.text.isNotEmpty()) {
                    val clipboardManager =
                        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipdata =
                        ClipData.newPlainText("text", fragvoicebinding.texttranslate.text.toString())
                    clipboardManager.setPrimaryClip(clipdata)
                    Toast.makeText(activity, "Text copied to clipboard", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(activity, "Empty Text Field", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.del->
            {
                fragvoicebinding.texttranslate.text.clear()
            }
            R.id.mic->
            {
                speak_()
            }
            R.id.history->
            {
                val intent = Intent(activity,VoiceTranslateHistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.bookmark->
            {
                val intent = Intent(activity,VoiceTranslateBookmarkActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onItemClick(Item: Languages) {
        if(prefs!!.lanselectionpos == 1)
        {
            fragvoicebinding.inputlanguage.text= Item.name
            fragvoicebinding.inputlanguageimage.setImageResource(Item.image)
            for (i in 0 until LangSelection.Langnames.size){
                if (Item.name.contains(LangSelection.Langnames[i])){
                    prefs!!.inputlangpos=i
                }
            }
        }


        else{
            fragvoicebinding.outputlanguage.text= Item.name
            fragvoicebinding.outputlanguageimage.setImageResource(Item.image)
            for (i in 0 until LangSelection.Langnames.size){
                if (Item.name.contains(LangSelection.Langnames[i])){
                    prefs!!.outputlangpos=i
                }
            }
        }
    }


}