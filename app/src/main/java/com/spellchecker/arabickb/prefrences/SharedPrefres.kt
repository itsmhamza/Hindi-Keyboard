package com.spellchecker.arabickb.prefrences

import android.content.Context
import android.content.SharedPreferences

class SharedPrefres( contxt:Context) {

    var prefrences:SharedPreferences=contxt.getSharedPreferences(Context.MODE_PRIVATE.toString(),
        Context.MODE_PRIVATE)

    var inputlangposition="inputlang"
    var outputlangposition="outputlang"
    var lanselection="langslectionpos"
    var lanname="langslectionposname"
    var outputlanname="langoutputposname"

    var lannameposition:String
        get() = prefrences.getString(lanname,"Arabic")!!
        set(value) = prefrences.edit().putString(lanname, value).apply()
    var outputlanposition:String
        get() = prefrences.getString(outputlanname,"English")!!
        set(value) = prefrences.edit().putString(outputlanname, value).apply()
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