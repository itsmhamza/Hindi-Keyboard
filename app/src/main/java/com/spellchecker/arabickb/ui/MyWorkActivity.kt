package com.spellchecker.arabickb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spellchecker.arabickb.databinding.ActivityMyWorkBinding

class MyWorkActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyWorkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
    }
}