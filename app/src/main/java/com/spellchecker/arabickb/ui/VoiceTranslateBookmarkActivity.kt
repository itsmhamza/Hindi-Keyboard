package com.spellchecker.arabickb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.adapters.VoiceBookmarkAdopter
import com.spellchecker.arabickb.adapters.VoiceHistoryAdopter
import com.spellchecker.arabickb.database.*
import com.spellchecker.arabickb.databinding.ActivityVoiceTranslateBookmarkBinding

class VoiceTranslateBookmarkActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityVoiceTranslateBookmarkBinding
    private lateinit var adopter: VoiceBookmarkAdopter
    lateinit var bookmarkviewmodel:BookmarkModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceTranslateBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener(this)
        val bookmarkdao= Speaktranslatedb.getDatabase(this).BookmarkDao()
        val bookmarkrepiostry= BookmarkRepoistry(bookmarkdao)
        bookmarkviewmodel= ViewModelProvider(this, BookmarkFactory(bookmarkrepiostry)).get(BookmarkModel::class.java)
        bookmarkviewmodel.getAllbookmarkData().observe(this, Observer {
            adopter = VoiceBookmarkAdopter(this,it)
            binding.recyclerview.adapter = adopter
            binding.recyclerview.layoutManager = LinearLayoutManager(this)
            binding.recyclerview.setHasFixedSize(true)
            binding.recyclerview.setItemViewCacheSize(20)
        })
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.back ->{
                finish()
            }
        }
    }
}