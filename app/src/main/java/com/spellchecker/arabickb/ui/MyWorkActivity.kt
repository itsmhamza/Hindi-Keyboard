package com.spellchecker.arabickb.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.spellchecker.arabickb.adapters.MyworkAdopter
import com.spellchecker.arabickb.databinding.ActivityMyWorkBinding
import com.spellchecker.arabickb.utils.mywork
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyWorkActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyWorkBinding
    private lateinit var adopter: MyworkAdopter
    val List : ArrayList<mywork> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addData()
        adopter = MyworkAdopter(this,List)
        binding.workrecyclerView.adapter = adopter
        binding.workrecyclerView.layoutManager = LinearLayoutManager(this)
        binding.workrecyclerView.setHasFixedSize(true)
        binding.workrecyclerView.setItemViewCacheSize(20)
        binding.back.setOnClickListener {
            finish()
        }
    }
    fun addData(){
        val dir = File(cacheDir, "images")
        if (dir.exists()) {
            for (f in dir.listFiles()) {
                val myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath())
                val lastModDate = Date(f.lastModified())
                List.add(mywork(myBitmap,f.nameWithoutExtension,lastModDate.toString()))
            }
        }
    }
}