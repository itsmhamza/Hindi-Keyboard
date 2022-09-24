package com.spellchecker.arabickb.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.adapters.LanguageSelectionAdapter
import com.spellchecker.arabickb.appinterfaces.onItemClickListener
import com.spellchecker.arabickb.databinding.ActivityLanguageListBinding
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.Languages

class LangSelectionDialog(var callback: onItemClickListener?): BottomSheetDialogFragment() {
    lateinit var lanlistbinding: ActivityLanguageListBinding
    var lanseleadapter: LanguageSelectionAdapter?=null
    var langlistner: onItemClickListener?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        lanlistbinding = ActivityLanguageListBinding.inflate(layoutInflater, container, false)
        langlistner=callback
        return lanlistbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiViewsData()
    }

    private fun uiViewsData() {
        lanlistbinding.rvlang.adapter = lanseleadapter
        lanlistbinding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lanseleadapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                lanseleadapter?.filter?.filter(newText)
                return true
            }
        })

        lanlistbinding.rvlang.apply {
            layoutManager= LinearLayoutManager(requireContext())
            lanseleadapter= LanguageSelectionAdapter(LangSelection.Lang,langlistner!!)
            lanlistbinding.rvlang.adapter=lanseleadapter
        }
    }
}