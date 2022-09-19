package com.spellchecker.arabickb.ui

import android.R.attr.*
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.*
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.adapters.emojiAdopter
import com.spellchecker.arabickb.databinding.ActivityImageEditorBinding
import com.spellchecker.arabickb.utils.EMOJIS.emojis
import com.xiaopo.flying.sticker.Sticker
import com.xiaopo.flying.sticker.TextSticker
import top.defaults.colorpicker.ColorPickerPopup
import top.defaults.colorpicker.ColorPickerPopup.ColorPickerObserver
import java.util.*


class ImageEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageEditorBinding
    private lateinit var adopter: emojiAdopter
    var emoji: ArrayList<String> = ArrayList()

    companion object {
        var Editimage: Bitmap? = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val TextSticker :ArrayList<Sticker> = arrayListOf(TextSticker(this))
        val emojiSticker :ArrayList<Sticker> = arrayListOf(TextSticker(this))
        emoji.addAll(emojis)

        binding.editimage.setImageBitmap(Editimage)

        binding.back.setOnClickListener {
            finish()
        }

        binding.addtext.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.add_text_layout)
            dialog.window!!.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
            val text = dialog.findViewById<EditText>(R.id.editText)
            val ok = dialog.findViewById<TextView>(R.id.ok)
            val cancel = dialog.findViewById<TextView>(R.id.cancel)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            ok.setOnClickListener {
                if(text.text.isNotEmpty()){
                    val sticker = TextSticker(this)
                    sticker.setTextColor(Color.BLACK)
                    sticker.text = "${text.text}"
                    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER)
                    sticker.resizeText()
                    binding.stickerview.addSticker(sticker)
                    TextSticker.add(sticker)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this, "Empty Text", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }

        binding.textcolor.setOnClickListener {
            ColorPickerPopup.Builder(this)
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(binding.stickerview, object : ColorPickerObserver() {
                    override fun onColorPicked(color: Int)
                    {
                        if (binding.stickerview.getCurrentSticker() is TextSticker) {
                            (binding.stickerview.getCurrentSticker() as TextSticker).setTextColor(color)
                            binding.stickerview.invalidate()
                        }
                    }
                    fun onColor(color: Int, fromUser: Boolean) {}
                })
        }
        binding.eraser.setOnClickListener {
            binding.stickerview.removeCurrentSticker()
        }
        binding.emoji.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.bottom_sheet_emoji)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            val recyclerview = dialog.findViewById<RecyclerView>(R.id.recyclerView)
            adopter = emojiAdopter(this,emoji,object : emojiAdopter.onItemClickListener{
                override fun onItemClick(Item: String) {
                    val sticker = TextSticker(this@ImageEditorActivity)
                    sticker.setTextColor(Color.WHITE)
                    sticker.text = "${EmojiCompat.get().process(Html.fromHtml(Item))}"
                    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER)
                    sticker.resizeText()
                    binding.stickerview.addSticker(sticker)
                    emojiSticker.add(sticker)
                    dialog.dismiss()
                }

            })
            recyclerview!!.adapter = adopter
            recyclerview.layoutManager = GridLayoutManager(this,4)
            recyclerview.setHasFixedSize(true)
            recyclerview.setItemViewCacheSize(20)
            dialog.show()
        }
    }


}
