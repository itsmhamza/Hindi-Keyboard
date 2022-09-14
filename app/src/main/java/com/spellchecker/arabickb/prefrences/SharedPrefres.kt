package com.spellchecker.arabickb.prefrences

import android.content.Context
import android.content.SharedPreferences

class SharedPrefres( contxt:Context) {

    var prefrences:SharedPreferences=contxt.getSharedPreferences(Context.MODE_PRIVATE.toString(),
        Context.MODE_PRIVATE)

    var inputlangposition="inputlang"
    var outputlangposition="outputlang"
    var lanselection="langslectionpos"

    var inputlangpos:Int
        get() = prefrences.getInt(inputlangposition, 2)
        set(value) = prefrences.edit().putInt(inputlangposition, value).apply()

    var outputlangpos: Int
        get() = prefrences.getInt(outputlangposition, 14)
        set(value) = prefrences.edit().putInt(outputlangposition, value).apply()

    var lanselectionpos: Int
        get() = prefrences.getInt(lanselection, 0)
        set(value) = prefrences.edit().putInt(lanselection, value).apply()
}