package com.example.ftc.fasturduenglishtranslator.utils

import android.content.Context
import android.content.SharedPreferences

var prefs: SharedPreferences? = null
fun Context.getMyPrefs(): SharedPreferences {
    return prefs ?: getSharedPreferences("kbprefs", Context.MODE_PRIVATE).also {
        prefs = it
    }
}


fun Context.editMyPrefs(editor: SharedPreferences.Editor.() -> Unit) {
    getMyPrefs().edit().apply {
        editor(this)
        apply()
    }
}
