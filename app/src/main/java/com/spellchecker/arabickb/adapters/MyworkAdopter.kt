package com.spellchecker.arabickb.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.ui.ImageEditorActivity
import com.spellchecker.arabickb.utils.mywork
import java.io.File


class MyworkAdopter(private val context: Context, val imagesList: ArrayList<mywork>) :
    RecyclerView.Adapter<MyworkAdopter.MyViewHolder>() {

    inner class MyViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var image: ImageView
        var edit: ImageView
        var share: ImageView
        var delete: ImageView
        var title: TextView
        var date: TextView

        init {
            image = parent.findViewById(R.id.workimage) as ImageView
            edit = parent.findViewById(R.id.edit) as ImageView
            share = parent.findViewById(R.id.share) as ImageView
            delete = parent.findViewById(R.id.delete) as ImageView
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
        val dir = File(context.cacheDir, "images")
        val child = dir.list()
        val file = File(dir,"${child[position]}")
        holder.image.setImageBitmap(work.image)
        holder.title.setText(work.name)
        holder.date.setText(work.date)
        holder.delete.setOnClickListener {
            file.delete()
            imagesList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.share.setOnClickListener {
            val contentUri: Uri = FileProvider.getUriForFile(context, "com.spellchecker.arabickb.provider", file)
            if (contentUri != null) {
                val shareIntent = Intent()
                shareIntent.setAction(Intent.ACTION_SEND)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                startActivity(context,Intent.createChooser(shareIntent, "Choose an app"),null)
            }
        }
        holder.edit.setOnClickListener {
            val intent = Intent(context,ImageEditorActivity::class.java)
            val myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
            ImageEditorActivity.Editimage = myBitmap
            file.delete()
            imagesList.removeAt(position)
            notifyDataSetChanged()
            startActivity(context,intent,null)
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }
}