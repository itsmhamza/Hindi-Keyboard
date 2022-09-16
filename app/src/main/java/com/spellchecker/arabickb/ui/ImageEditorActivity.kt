package com.spellchecker.arabickb.ui

import android.R.attr.*
import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.databinding.ActivityImageEditorBinding


class ImageEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageEditorBinding

    companion object {
        var Editimage: Bitmap? = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editimage.setImageBitmap(Editimage)
        binding.addtext.setOnClickListener {
            binding.addtextt.text.clear()
            binding.addtextt.visibility = View.VISIBLE
            binding.addtextt.setFocusableInTouchMode(true);
            binding.addtextt.setFocusable(true);
            binding.addtextt.requestFocus();
        }
        binding.eraser.setOnClickListener {
            binding.addtextt.visibility = View.GONE
        }
        binding.textcolor.setOnClickListener {
            binding.addtextt.setTextColor(Color.parseColor("#bdbdbd"));
        }
    }

}
