package dev.patrickgold.florisboard.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.ime.core.Subtype
import java.util.*

fun isAndroidM() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun getamilSubtype(isShift: Boolean = false): Subtype {
    val mode = when {
        isShift -> "telugushift"
        else -> "hindi"
    }
    return Subtype(-1, Locale.ENGLISH, mode)
}


fun String.showAsToast(context: Context?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).apply {
        ((view as ViewGroup).getChildAt(0) as TextView).apply {
            text = this@showAsToast
            gravity = Gravity.CENTER
        }
        show()
    }
}

// startnewActivity
inline fun <reified A : Activity> Context.startNewActivity() {
    this.startActivity(Intent(this, A::class.java))
}


inline fun <reified A : Activity> Context.startActivityWithExtras(extras: Intent.() -> Unit) {
    val intent = Intent(this, A::class.java)
    extras(intent)
    this.startActivity(intent)
}

//// spinner setup
fun Spinner.setUpSpinner(
    context: Context,
    list: List<String>,
    listener: AdapterView.OnItemSelectedListener,
    selection: Int = 0
) {

    val adapter: ArrayAdapter<*> = object : ArrayAdapter<Any?>(
        context,
        android.R.layout.simple_list_item_1,
        list
    ) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return super.getDropDownView(position, convertView, parent).apply {
                (this as TextView).apply {
                    layoutParams.height = (ViewLayoutUtils.convertDpToPixel(28f, context)).toInt()
                }
            }
        }
    }
    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

    this.adapter = adapter
    this.onItemSelectedListener = listener
    this.setSelection(selection)
}

fun RecyclerView.adapterAndManager(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    isHorizontal: Boolean = false,
    isGrid: Boolean = false,
    spanCount: Int = 3
) {
    this.layoutManager =
        when {
            isGrid -> GridLayoutManager(context, spanCount)
            isHorizontal -> LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(context)
        }
    this.itemAnimator = DefaultItemAnimator()
    this.adapter = adapter
}
