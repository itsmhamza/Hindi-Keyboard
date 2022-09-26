package com.spellchecker.arabickb.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.spellchecker.arabickb.databinding.FragImageEditorBinding
import com.spellchecker.arabickb.ui.ImageEditorActivity
import com.spellchecker.arabickb.ui.MyWorkActivity
import java.io.IOException
import java.util.*


class ImageEditorFragment:Fragment() {

    lateinit var fragimagebinding: FragImageEditorBinding
    var  GALLERY_REQUEST = 12
    private val CAMERA_REQUEST = 11

    companion object {

        fun newInstance() =
            ImageEditorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragimagebinding= FragImageEditorBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return fragimagebinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPermission()
        fragimagebinding.gallery.setOnClickListener {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
        }
        fragimagebinding.camera.setOnClickListener {
           pickCameraImageedit()
        }
        fragimagebinding.myWork.setOnClickListener {
            val intent = Intent(activity,MyWorkActivity::class.java)
            startActivity(intent)
        }
        fragimagebinding.rate.setOnClickListener {
            try{
            val uri = Uri.parse("http://play.google.com/store/apps/details?id=${activity?.packageName}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            }catch (e:Exception){
                ///
            }
        }
        fragimagebinding.share.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hindi English Speak Translate")
                var shareMessage ="https://play.google.com/store/apps/details?id=${activity?.packageName}"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {
                //e.toString();
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (data != null) {
                        val intent = Intent(activity, ImageEditorActivity::class.java)
                        try {
                            val uri = data?.data
                            val bitmap = MediaStore.Images.Media.getBitmap(activity?.getContentResolver(), uri)
                            ImageEditorActivity.Editimage = bitmap
                            startActivity(intent)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                }
            }
            GALLERY_REQUEST -> {
                if (data != null) {
                    val intent = Intent(activity, ImageEditorActivity::class.java)
                    val selectedImage = data.data!!
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(
                                activity?.contentResolver,
                                selectedImage
                            )
                        ImageEditorActivity.Editimage = bitmap
                        startActivity(intent)
                    } catch (e: IOException) {

                    }
                }
            }
        }
    }
    private fun getPermission() {
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (report.areAllPermissionsGranted()) {

                        } else {
                        }

                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener{

            }.check()

    }
    @Suppress("UNUSED_PARAMETER")
    private fun pickCameraImageedit() {
        ImagePicker.with(this)
            .cameraOnly()
            .start(CAMERA_REQUEST)
    }
    }