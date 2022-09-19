package com.spellchecker.arabickb.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.spellchecker.arabickb.R
import com.spellchecker.arabickb.databinding.ActivityMainBinding
import com.spellchecker.arabickb.databinding.PermiisionlayoutBinding
import com.spellchecker.arabickb.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var mainbinding:ActivityMainBinding
    lateinit var navController: NavController
    var perdialogscreen : PersmissionDiaog?=null
    private val PERMISSIONREQUEST= 48
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainbinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainbinding.root)
        requestPermission()
        uiAttachViews()

    }

    private fun uiAttachViews() {
        mainbinding.viewPager.adapter = FragmentAdapter(this)

        TabLayoutMediator(mainbinding.tabLayout, mainbinding.viewPager){tab, position ->
            when(position){

                0->
                    tab.text = resources.getString(R.string.voicetransation)
                1->
                    tab.text = resources.getString(R.string.texttranslation)
                2->
                    tab.text = resources.getString(R.string.dictionary)
                3->
                    tab.text = resources.getString(R.string.imageeditor)
                4->
                    tab.text = resources.getString(R.string.learneng)
            }

        }.attach()

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    class FragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){
        override fun getItemCount(): Int {
            return 5
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> SpeakTranslateFragment.newInstance()
                1 -> VoiceTranslateFragment.newInstance()
                2 -> DictionaryFragment.newInstance()
                3 -> ImageEditorFragment.newInstance()
                else -> PhraseBookFragment.newInstance()
            }
        }

    }

    fun requestPermission(){
        Dexter.withContext(this)
            .withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted, open the camera
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        // navigate user to app settings
                    }
                    showPermissionDialog()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                    showPermissionDialog()
                }
            }).check()
    }
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, PERMISSIONREQUEST)
    }

    private fun showPermissionDialog() {
        perdialogscreen = PersmissionDiaog(this,this)
        perdialogscreen?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        perdialogscreen?.setCancelable(false)
        perdialogscreen?.show()

    }
    inner class PersmissionDiaog (val mContext: Context, activity: Activity): Dialog(mContext) {

        private lateinit var permissionbinding: PermiisionlayoutBinding
        var mactivity: Activity =activity


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            permissionbinding = PermiisionlayoutBinding.inflate(layoutInflater)
            setContentView(permissionbinding.root)
            permissionbinding.btnRead.setOnClickListener {
                perdialogscreen!!.cancel()
                openSettings()
            }

        }
    }
}