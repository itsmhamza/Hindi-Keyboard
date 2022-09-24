package com.spellchecker.arabickb.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.utils.mywork

class MyworkAdopter(private val context: Context, val imagesList: ArrayList<mywork>) :
    RecyclerView.Adapter<MyworkAdopter.MyViewHolder>() {

    inner class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var image: ImageView
        var title: TextView
        var date: TextView

        init {
            image = parent.findViewById(R.id.workimage) as ImageView
            title = parent.findViewById(R.id.titlee) as TextView
            date = parent.findViewById(R.id.date) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.mywork_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val work = imagesList[position]
        holder.image.setImageBitmap(work.image)
        holder.title.setText(work.name)
        holder.date.setText(work.date)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }
}