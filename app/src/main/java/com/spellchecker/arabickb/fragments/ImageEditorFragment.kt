package com.spellchecker.arabickb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.spellchecker.arabickb.databinding.FragImageEditorBinding



class ImageEditorFragment:Fragment() {

    lateinit var fragimagebinding: FragImageEditorBinding

    companion object {

        fun newInstance() =
            ImageEditorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragimagebinding= FragImageEditorBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return fragimagebinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}