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
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import dev.patrickgold.florisboard.ime.core.FlorisBoard


class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var mainbinding:ActivityMainBinding
    lateinit var navController: NavController
    var perdialogscreen : PersmissionDiaog?=null
    private val PERMISSIONREQUEST= 48
    var mSlideState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainbinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainbinding.root)
        requestPermission()
        uiAttachViews()
        drawer()
        init()
    }

    private fun init() {
        mainbinding.toolbarmain.ivmenu.setOnClickListener(this)
        mainbinding.menu.share.setOnClickListener(this)
        mainbinding.menu.rateus.setOnClickListener(this)
        mainbinding.menu.aboutus.setOnClickListener(this)
        mainbinding.menu.notification.setOnClickListener(this)
        mainbinding.menu.privacy.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivmenu->
                mainbinding.drawerLayout.openDrawer(Gravity.LEFT)
            R.id.share->
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
            R.id.rateus->
                Toast.makeText(this, "rateus", Toast.LENGTH_SHORT).show()
            R.id.aboutus->
                Toast.makeText(this, "about us", Toast.LENGTH_SHORT).show()
            R.id.notification->
                Toast.makeText(this, "notification", Toast.LENGTH_SHORT).show()
            R.id.privacy->
                Toast.makeText(this, "privacy", Toast.LENGTH_SHORT).show()
        }
    }

    private fun drawer() {
        mainbinding.drawerLayout.setDrawerListener(object : ActionBarDrawerToggle(
            this,
            mainbinding.drawerLayout,
            0,
            0) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                mSlideState = false //is Closed
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                mSlideState = true //is Opened
            }
        })
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
                5->
                    tab.text = resources.getString(R.string.keyboard)
            }

        }.attach()

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    class FragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){
        override fun getItemCount(): Int {
            return 6
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> SpeakTranslateFragment.newInstance()
                1 -> VoiceTranslateFragment.newInstance()
                2 -> DictionaryFragment.newInstance()
                3 -> ImageEditorFragment.newInstance()
                4 -> PhraseBookFragment.newInstance()
                else -> KeyboardFragment.newInstance()
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
private fun toobalTitle(){

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