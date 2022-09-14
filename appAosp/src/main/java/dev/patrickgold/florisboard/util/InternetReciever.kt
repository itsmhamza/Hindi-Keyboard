package dev.patrickgold.florisboard.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class InternetReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action== ConnectivityManager.CONNECTIVITY_ACTION){
            context?.showToast("change")
        }
    }
}