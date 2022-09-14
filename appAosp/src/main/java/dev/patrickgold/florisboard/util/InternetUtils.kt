package dev.patrickgold.florisboard.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi

var connectivityCallback: ConnectivityManager.NetworkCallback? = null

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun ConnectivityManager.internetAvailability(callback: (Boolean) -> Unit) {
    connectivityCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            callback.invoke(true)
        }

        override fun onLost(network: Network) {
            callback.invoke(false)
        }
    }
    registerNetworkCallback(
        NetworkRequest.Builder().build(),
        connectivityCallback as ConnectivityManager.NetworkCallback
    )
}

fun ConnectivityManager.removeCallback() {
    connectivityCallback = null
}

fun Context.isInternetOn(): Boolean {
    val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        capabilities?.let {
            return when {
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        it.hasTransport(
                            NetworkCapabilities.TRANSPORT_ETHERNET
                        ) -> true

                else -> false
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}


fun getConnectivityFilter() =
    IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

