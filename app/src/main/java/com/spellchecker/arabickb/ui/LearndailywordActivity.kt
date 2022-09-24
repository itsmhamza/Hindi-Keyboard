package com.spellchecker.arabickb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spellchecker.arabickb.databinding.ActivityLearndailywordBinding

class LearndailywordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLearndailywordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearndailywordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener {
            finish()
        }
    }
}