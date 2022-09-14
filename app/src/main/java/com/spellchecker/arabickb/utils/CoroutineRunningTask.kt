package com.spellchecker.arabickb.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoroutineRunningTask (val context: Context)
{
    private lateinit var result: String

    fun run(runCoroutineTask: RunCoroutineTask)
    {
        val process = CoroutineScope(Dispatchers.Default).launch {
            result = runCoroutineTask.onRun()
        }
        process.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                runCoroutineTask.onComplete(result)
            }
        }
    }

    interface RunCoroutineTask
    {
        fun onRun(): String
        fun onComplete(data: String)
    }

}