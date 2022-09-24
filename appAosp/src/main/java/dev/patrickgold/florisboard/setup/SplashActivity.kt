package dev.patrickgold.florisboard.setup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import dev.patrickgold.florisboard.R


class SplashActivity : AppCompatActivity() {

    lateinit var getStartedBtn: Button
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_main)
       // showNative()

        progressBar = findViewById(R.id.progressBar)
        getStartedBtn = findViewById(R.id.getStartedBtn)

        getStartedBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({

            progressBar.visibility = View.GONE
            getStartedBtn.visibility = View.VISIBLE

        }, 7000)

    }
}