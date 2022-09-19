package com.spellchecker.arabickb.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.database.SharedViewModel
import com.spellchecker.arabickb.database.SpeakModelFactory
import com.spellchecker.arabickb.database.SpeakRepositry
import com.spellchecker.arabickb.database.Speaktranslatedb
import com.spellchecker.arabickb.databinding.FragKeyboardBinding
import com.spellchecker.arabickb.databinding.FragVoiceTranslateBinding
import com.spellchecker.arabickb.prefrences.SharedPrefres
import com.spellchecker.arabickb.ui.LanguageListActivity
import com.spellchecker.arabickb.utils.LangSelection
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.theme.Theme
import dev.patrickgold.florisboard.setup.UncachedInputMethodManagerUtils

class KeyboardFragment:Fragment(),View.OnClickListener  {

    lateinit var keyboardbinding:FragKeyboardBinding
    private var mImm: InputMethodManager? = null
    var contxt: Context?=null
    var setup_step = 1

    companion object {

        fun newInstance() =
            KeyboardFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        keyboardbinding= FragKeyboardBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return keyboardbinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        uiViews()

//        keyboardbinding.cardView2.setOnClickListener {
//
//            Toast.makeText(requireContext(), "okay", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun uiViews() {
        contxt=requireActivity()
        keyboardbinding.cardView2.setOnClickListener(this)
        keyboardbinding.cardView1.setOnClickListener(this)
        keyboardbinding.cardView3.setOnClickListener(this)
        mImm = contxt!!.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cardView2->
                if (setup_step == 1) {
                    /////////////////       launch input and settings activity      ////////////////////
                    enableKeyboard()
                    val intent = Intent(
                        contxt,
                        FlorisBoard::class.java
                    ) //  to relaunch app on keyboard selection
                    contxt!!.startService(intent)
                } else {
                    /////////////////       launch input method picker dialog      ////////////////////
                    val im = contxt!!.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.showInputMethodPicker()
                }


            R.id.cardView1->{
                if (setup_step != 1) {
                    /////////////////       launch input method picker dialog      ////////////////////
                    val im = contxt!!.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.showInputMethodPicker()

                }
            }

            R.id.cardView3-> {
                Toast.makeText(contxt, "Todo // Redirect to where... ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ///////////////////////     launch input and language settings      //////////////////////////////////
    private fun enableKeyboard() {
        startActivityForResult(Intent("android.settings.INPUT_METHOD_SETTINGS"), 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onResume() {
        ////////////////////////        show current setup step     ///////////////////////////
        //    updateImeIssueCardsVisibilities()
        super.onResume()
        checkKeybordExit()
        setScreen()
    }

    /////////////////////////      show current setup screen according to enabled/disabled settings     //////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun setScreen() {
        when (setup_step) {
            1 -> setupScreen1()
            2 -> setupScreen2()
            3 -> setupScreen3()
        }
    }

    //////////////////////      to check custom keyboard is added to settings or not        /////////////////////////////////////
    private fun checkKeybordExit() {
        if (UncachedInputMethodManagerUtils.isThisImeEnabled(contxt!!, mImm!!)) {
            setup_step = 2
            if (isInputMethodEnabled()) {
                setup_step = 3
            }
        } else {
            setup_step = 1
        }
    }


    //////////////////////////////////      to check custom ime enabled or not       /////////////////////////////////
    private fun isInputMethodEnabled(): Boolean {
        val id = Settings.Secure.getString(
            contxt!!.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val defaultInputMethod = ComponentName.unflattenFromString(id)
        val myInputMethod = ComponentName(contxt!!, FlorisBoard::class.java)
        return myInputMethod == defaultInputMethod
    }


    private fun setupScreen3() {
        keyboardbinding.step1.visibility = View.GONE
        keyboardbinding.step2.visibility = View.GONE
        keyboardbinding.congo.visibility = View.VISIBLE
    }

    private fun setupScreen2() {
        keyboardbinding.step1.visibility = View.GONE
        keyboardbinding.step2.visibility = View.VISIBLE
        keyboardbinding.congo.visibility = View.GONE
    }

    private fun setupScreen1() {

        keyboardbinding.step1.visibility = View.VISIBLE
        keyboardbinding.step2.visibility = View.GONE
        keyboardbinding.congo.visibility = View.GONE
    }


}