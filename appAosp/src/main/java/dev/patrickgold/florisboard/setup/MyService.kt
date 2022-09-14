package  dev.patrickgold.florisboard.setup

import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.core.app.JobIntentService



class MyService : JobIntentService() {
    val ACTION_STARTED_FROM_SERVICE = "action_service"
    override fun onHandleWork(intent: Intent) {
        val packageLocal = packageName
        var isInputDeviceEnabled = false
        while (!isInputDeviceEnabled) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            val list = inputMethodManager.enabledInputMethodList

            // check if our keyboard is enabled as input method
            for (inputMethod in list) {
                val packageName = inputMethod.packageName
                if (packageName == packageLocal) {
                    isInputDeviceEnabled = true
                }
            }
        }
        // open activity
        val newIntent = Intent(this, MainActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        newIntent.action = ACTION_STARTED_FROM_SERVICE
        startActivity(newIntent)
        MainActivity.finishActivity()
    }
}