package com.spellchecker.arabickb.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.spellchecker.arabickb.adapters.LanguageSelectionAdapter
import com.spellchecker.arabickb.databinding.ActivityLanguageListBinding
import com.spellchecker.arabickb.utils.LangSelection
import com.spellchecker.arabickb.utils.Languages

class LanguageListActivity : AppCompatActivity(), LanguageSelectionAdapter.onItemClickListener {
    lateinit var lanlistbinding:ActivityLanguageListBinding
     var lanseleadapter:LanguageSelectionAdapter?=null
    var langlistner:LanguageSelectionAdapter.onItemClickListener?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lanlistbinding=ActivityLanguageListBinding.inflate(layoutInflater)
        setContentView(lanlistbinding.root)
        langlistner=this
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
            layoutManager=LinearLayoutManager(this@LanguageListActivity)
            lanseleadapter= LanguageSelectionAdapter(LangSelection.Lang,langlistner!!)
            lanlistbinding.rvlang.adapter=lanseleadapter
        }
    }

    override fun onItemClick(Item: Languages) {
Toast.makeText(this@LanguageListActivity,Item.name,Toast.LENGTH_SHORT).show()
    }
}