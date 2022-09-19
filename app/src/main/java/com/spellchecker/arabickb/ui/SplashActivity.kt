package com.spellchecker.arabickb.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.ftc.fasturduenglishtranslator.utils.editMyPrefs
import com.example.ftc.fasturduenglishtranslator.utils.getMyPrefs
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.databinding.ActivitySplashBinding
import dev.patrickgold.florisboard.ime.core.PrefHelper
import dev.patrickgold.florisboard.ime.theme.Theme

class SplashActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var splashbinding:ActivitySplashBinding
    private var isFirstTime = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashbinding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashbinding.root)
        uiChanges()

        isFirstTime = this.getMyPrefs().getBoolean("isFirstTime", true)
//        if (isFirstTime) {
//            this.editMyPrefs {
//                putBoolean("isFirstTime", false)
//            }
//            Theme.writeThemeToPrefs(
//                PrefHelper.getDefaultInstance(this),
//                Theme.fromJsonFile(this, "ime/theme/floris_day.json")!!
//            )
//        }
    }
    private fun uiChanges() {
        splashbinding.btnstart.setOnClickListener(this)
        Handler(Looper.getMainLooper()).postDelayed({
            splashbinding.btnstart.visibility=View.VISIBLE
            splashbinding.progressBar.visibility=View.GONE
        },5000)
    }
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnstart ->
                startActivity(Intent(this,MainActivity::class.java))

        }
    }
}