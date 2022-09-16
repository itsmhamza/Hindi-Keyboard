package com.spellchecker.arabickb.ui

import android.R.attr.*
import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.databinding.ActivityImageEditorBinding
import com.xiaopo.flying.sticker.BitmapStickerIcon
import com.xiaopo.flying.sticker.Sticker
import com.xiaopo.flying.sticker.StickerIconEvent
import com.xiaopo.flying.sticker.TextSticker
import java.util.*


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


    }

}
