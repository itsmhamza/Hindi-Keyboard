package com.spellchecker.arabickb.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spellchecker.arabickb.databinding.FragDictionaryBinding
import com.spellchecker.arabickb.ui.LearndailyquizActivity
import com.spellchecker.arabickb.ui.LearndailywordActivity


class DictionaryFragment:Fragment() {
    lateinit var fragdicbinding:FragDictionaryBinding
    companion object {

        fun newInstance() =
            DictionaryFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragdicbinding= FragDictionaryBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return fragdicbinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragdicbinding.wordCard.setOnClickListener {
            val intent = Intent(activity,LearndailywordActivity::class.java)
            startActivity(intent)
        }

        fragdicbinding.quizCard.setOnClickListener {
            val intent = Intent(activity, LearndailyquizActivity::class.java)
            startActivity(intent)
        }
    }
}