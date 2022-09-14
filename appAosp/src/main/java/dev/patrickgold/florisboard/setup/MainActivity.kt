package  dev.patrickgold.florisboard.setup

import android.Manifest
import android.app.Activity
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.core.PrefHelper
import dev.patrickgold.florisboard.ime.theme.Theme
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    var setup_step = 1
    private var mImm: InputMethodManager? = null
    private var mReceiver: InputMethodChangeReceiver? = null
    private val PERMISSION_REQUEST_CODE: Int = 101
    lateinit var prefs: PrefHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // InterstitialAdUpdated.getInstance().showInterstitialAdNew(this)
        requestPermission()
        //showNative()


        prefs = PrefHelper(this)
        prefs.initDefaultPreferences()

        val mode = when (prefs.advanced.settingsTheme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "auto" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        mReceiver = InputMethodChangeReceiver()
        val filter = IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED)
        registerReceiver(mReceiver, filter)
        mImm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        addBtn.setOnClickListener {
            if (setup_step == 1) {
                /////////////////       launch input and settings activity      ////////////////////
                enableKeyboard()
                val intent = Intent(
                    this@MainActivity,
                    FlorisBoard::class.java
                ) //  to relaunch app on keyboard selection
                startService(intent)
            } else {
                /////////////////       launch input method picker dialog      ////////////////////
                val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                im.showInputMethodPicker()
            }
        }

        disableBtn.setOnClickListener {
            if (setup_step != 1) {
                /////////////////       launch input method picker dialog      ////////////////////
                val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                im.showInputMethodPicker()
            }
        }

        doneBtn.setOnClickListener {
            finish()
        }
    }

    //////////////////////////////////      to check custom ime enabled or not       /////////////////////////////////
    private fun isInputMethodEnabled(): Boolean {
        val id = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val defaultInputMethod = ComponentName.unflattenFromString(id)
        val myInputMethod = ComponentName(this, FlorisBoard::class.java)
        return myInputMethod == defaultInputMethod
    }

    //////////////////////      to check custom keyboard is added to settings or not        /////////////////////////////////////
    private fun checkKeybordExit() {
        if (UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm!!)) {
            setup_step = 2
            if (isInputMethodEnabled()) {
                setup_step = 3
            }
        } else {
            setup_step = 1
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onResume() {
        super.onResume()
        ////////////////////////        show current setup step     ///////////////////////////
        //    updateImeIssueCardsVisibilities()
        checkKeybordExit()
        setScreen()
    }

    //////////////////////////      input method change receiver to listen ime changes      //////////////////////////////////////
    inner class InputMethodChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_INPUT_METHOD_CHANGED) {
                setup_step = if (isInputMethodEnabled()) {
                    3
                } else {
                    2
                }
                setScreen()
            }
        }
    }

    /////////////////////////      show current setup screen according to enabled/disabled settings     //////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun setScreen() {
        when (setup_step) {
            1 -> setupScreen1()
            2 -> setupScreen2()
            3 -> setupScreen3()
        }
    }

    private fun setupScreen3() {
        addBtn.visibility = View.GONE
        addTextTv.visibility = View.GONE
        addTitleTv.visibility = View.GONE
        doneBtn.visibility = View.VISIBLE
        disableBtn.visibility = View.VISIBLE
        doneTextTv.visibility = View.VISIBLE
        doneTextTv2.visibility = View.VISIBLE
        // Set theme to floris_day
        Theme.writeThemeToPrefs(
            this.prefs,
            Theme.fromJsonFile(this, "ime/theme/floris_day.json")!!
        )
    }

    private fun setupScreen2() {
        addBtn.setBackgroundResource(R.drawable.active_btn)
        addTextTv.text = getString(R.string.activeTxtStr)
        addBtn.visibility = View.VISIBLE
        addTextTv.visibility = View.VISIBLE
        addTitleTv.visibility = View.VISIBLE
        doneBtn.visibility = View.GONE
        disableBtn.visibility = View.GONE
        doneTextTv.visibility = View.GONE
        doneTextTv2.visibility = View.GONE
    }

    private fun setupScreen1() {
        addBtn.setBackgroundResource(R.drawable.add_btn)
        addTextTv.text = getString(R.string.add_english_typing_in_your_settings)
        addBtn.visibility = View.VISIBLE
        addTextTv.visibility = View.VISIBLE
        addTitleTv.visibility = View.VISIBLE
        doneBtn.visibility = View.GONE
        disableBtn.visibility = View.GONE
        doneTextTv.visibility = View.GONE
        doneTextTv2.visibility = View.GONE
        // Set theme to floris_day

    }

    ///////////////////////     launch input and language settings      //////////////////////////////////
    private fun enableKeyboard() {
        startActivityForResult(Intent("android.settings.INPUT_METHOD_SETTINGS"), 0)
    }

    //////////////////////////      input method change receiver to listen ime changes      //////////////////////////////////////
//    inner class InputMethodChangeReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (action == Intent.ACTION_INPUT_METHOD_CHANGED) {
//                setup_step = if (updateImeIssueCardsVisibilities()) {
//                    3
//                } else {
//                    2
//                }
//                setScreen()
//            }
//        }
//    }
    private fun updateImeIssueCardsVisibilities() {
        val isImeEnabled = FlorisBoard.checkIfImeIsEnabled(this)
        val isImeSelected = FlorisBoard.checkIfImeIsSelected(this)
        addBtn.visibility =
            if (isImeEnabled) {
                View.GONE
            } else {
                View.VISIBLE
            }
        addBtn.visibility =
            if (!isImeEnabled || isImeSelected) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    init {
        var activity: Activity

    }

    companion object {
        fun finishActivity() {
            finishActivity()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {

        }
    }

}