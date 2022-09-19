package com.spellchecker.arabickb.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.adapters.LanguageSelectionAdapter
import com.spellchecker.arabickb.adapters.VoiceAdapter
import com.spellchecker.arabickb.database.*
import com.spellchecker.arabickb.databinding.FragSpTranslateBinding
import com.spellchecker.arabickb.prefrences.SharedPrefres
import com.spellchecker.arabickb.ui.LanguageListActivity
import com.spellchecker.arabickb.utils.CoroutineRunningTask
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.LangSelection.Lang
import com.spellchecker.arabickb.utils.Languages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.util.*


class SpeakTranslateFragment:Fragment(),View.OnClickListener,LanguageSelectionAdapter.onItemClickListener
     {
    lateinit var fragspbinding:FragSpTranslateBinding
    var adaptervoice: VoiceAdapter?=null
    var inputtype:Int=1
    var htttpclclient = OkHttpClient()
   var prefs:SharedPrefres?=null
    var inputlangcode: String? =null
    var outputlangcode: String? =null
    var contxt:Context?=null
    lateinit var sharedmodel:SharedViewModel

    companion object {

        fun newInstance() =
            SpeakTranslateFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragspbinding=FragSpTranslateBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return fragspbinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiViews()
        attachAdapter()
    }

    private fun uiViews() {
        contxt=requireActivity()
        prefs= SharedPrefres(requireContext())
        val dao= Speaktranslatedb.getDatabase(requireActivity()).SpeakDao()
        val repositry=SpeakRepositry(dao)
        sharedmodel= ViewModelProvider(this, SpeakModelFactory(repositry)).get(SharedViewModel::class.java)
        fragspbinding.leftview.setOnClickListener(this)
        fragspbinding.rightview.setOnClickListener(this)
        fragspbinding.leftmic.setOnClickListener(this)
        fragspbinding.rightmic.setOnClickListener(this)
        fragspbinding.ivswitch.setOnClickListener(this)
        fragspbinding.leftlabel.text= LangSelection.Langnames[prefs!!.inputlangpos]
        fragspbinding.flagleft.setImageResource(LangSelection.Flagimg[prefs!!.inputlangpos])
        fragspbinding.rightlabel.text= LangSelection.Langnames[prefs!!.outputlangpos]
        fragspbinding.flagright.setImageResource(LangSelection.Flagimg[prefs!!.outputlangpos])
    }

    override fun onClick(v: View?) {
        when(v?.id){
           R.id.leftmic->
               voiceInputPrompt(1)
            R.id.rightmic->
                voiceInputPrompt(2)
            R.id.ivswitch->
                switchLanguages()
            R.id.leftview->
            {
                prefs!!.lanselectionpos=1
                startActivity(Intent(requireActivity(), LanguageListActivity::class.java))
            }
            R.id.rightview->
            {
                prefs!!.lanselectionpos=2
                startActivity(Intent(requireActivity(), LanguageListActivity::class.java))
            }

        }
    }

    fun voiceInputPrompt(lanpos: Int){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        inputtype=lanpos

        if (lanpos == 1)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LangSelection.Langcode[prefs!!.inputlangpos])
        else
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LangSelection.Langcode[prefs!!.outputlangpos])
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.app_name)
        )
        try {
            onResultLaunchActivity.launch(intent)
            // startActivityForResult(intent, REQCODEINPUT)
        } catch (a: ActivityNotFoundException) {

        }
    }
    var onResultLaunchActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            CoroutineScope(Dispatchers.IO).launch {
                voiceinputResult(result!![0])


            }
        }
    }

    private fun switchLanguages() {
        var intemp=fragspbinding.leftlabel.text.toString()
        fragspbinding.leftlabel.text=fragspbinding.rightlabel.text
        fragspbinding.rightlabel.text= intemp
        var inpulantemp=prefs!!.inputlangpos
        prefs!!.inputlangpos=prefs!!.outputlangpos
        prefs!!.outputlangpos= inpulantemp
        fragspbinding.flagleft.setImageResource(LangSelection.Flagimg[prefs!!.inputlangpos])
        fragspbinding.flagright.setImageResource(LangSelection.Flagimg[prefs!!.outputlangpos])
    }
    private fun voiceinputResult(word: String) {
        if(inputtype==1) {
            inputlangcode = LangSelection.Langcode.get(prefs!!.inputlangpos)
            outputlangcode=LangSelection.Langcode.get(prefs!!.outputlangpos)
        }
        else{
            inputlangcode = LangSelection.Langcode.get(prefs!!.outputlangpos)
            outputlangcode=LangSelection.Langcode.get(prefs!!.inputlangpos)
        }

        val url = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=" + inputlangcode + "&" +
                "tl=" + outputlangcode +
                "&dt=t&q=" + word.trim { it <= ' ' }.replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8"

        voiceInutpuResult(url,word)

    }
    fun voiceInutpuResult(url: String,wordinput:String){

        CoroutineRunningTask(requireActivity()).run(object : CoroutineRunningTask.RunCoroutineTask
        {
            override fun onRun(): String
            {

                val builder: okhttp3.Request.Builder = okhttp3.Request.Builder()
                builder.url(url)
                val request: okhttp3.Request = builder.build()
                try
                {
                    val response = htttpclclient.newCall(request).execute()
                    val jsonArray = JSONArray(Objects.requireNonNull(response.body)?.string())
                    val jsonArray2: JSONArray = jsonArray.getJSONArray(0)
                    val jsonArray3: JSONArray = jsonArray2.getJSONArray(0)
                    var data = ""
                    data = data + jsonArray3.getString(0)
                    return data
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                return ""
            }

            override fun onComplete(data: String)
            {

                if (data.isEmpty() || data.equals("null") || data.equals("Null"))
                {
                    Toast.makeText(requireActivity(), "There seems to be network issue", Toast.LENGTH_SHORT).show()

                }
                else
                {

                    if (inputtype==1){
                        val voiceData = Speaktranslation(0,
                            wordinput,
                            data,
                            prefs!!.inputlangpos,
                            prefs!!.outputlangpos,
                            1
                        )
                        sharedmodel.insertVoiceTranslation(voiceData)

                    }
                    else{

                        val voiceData = Speaktranslation(
                            0,
                            wordinput,
                            data,
                            prefs!!.outputlangpos,
                            prefs!!.inputlangpos,
                            2,

                            )
                        sharedmodel.insertVoiceTranslation(voiceData)

                    }
                    attachAdapter()
                }

            }
        })
    }

    private fun attachAdapter() {

        sharedmodel!!.getvoicetranslation().observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.size<=0){
                fragspbinding.rv.visibility=View.GONE
                fragspbinding.emptyview.visibility=View.VISIBLE

            }
            else{
                fragspbinding.rv.visibility=View.VISIBLE
                fragspbinding.emptyview.visibility=View.GONE
            }

            fragspbinding.rv.apply {
                layoutManager=LinearLayoutManager(requireActivity())
                adaptervoice= VoiceAdapter(it,sharedmodel)
                fragspbinding.rv.adapter=adaptervoice
            }
        })


    }

         override fun onItemClick(Item: Languages) {
             fragspbinding.leftlabel.text= Item.name
             fragspbinding.flagleft.setImageResource(Item.image)
         }


     }