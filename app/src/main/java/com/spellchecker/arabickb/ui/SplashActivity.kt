package com.spellchecker.arabickb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var splashbinding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashbinding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashbinding.root)

    }
}