package com.spellchecker.arabickb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.adapters.VoiceHistoryAdopter
import com.spellchecker.arabickb.database.*
import com.spellchecker.arabickb.databinding.ActivityVoiceTranslateHistoryBinding

class VoiceTranslateHistoryActivity : AppCompatActivity() , View.OnClickListener{
    private lateinit var binding: ActivityVoiceTranslateHistoryBinding
    private lateinit var adopter:VoiceHistoryAdopter
    lateinit var historyviewModel:HistoryModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceTranslateHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener(this)
        val histrydao= Speaktranslatedb.getDatabase(this).HistoryDao()
        val historyRepoistry= HistoryRepoistry(histrydao)
        historyviewModel= ViewModelProvider(this, HistoryFactory(historyRepoistry)).get(HistoryModel::class.java)
        historyviewModel.getAllHistryData().observe(this, Observer {
            adopter = VoiceHistoryAdopter(this,it)
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