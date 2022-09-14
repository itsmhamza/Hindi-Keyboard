package com.spellchecker.arabickb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spellchecker.arabickb.databinding.FragPhraseBookBinding


class PhraseBookFragment:Fragment() {

    lateinit var fragphrasebinding: FragPhraseBookBinding

    companion object {

        fun newInstance() =
            PhraseBookFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragphrasebinding= FragPhraseBookBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return fragphrasebinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}