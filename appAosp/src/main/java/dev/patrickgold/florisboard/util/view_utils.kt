package dev.patrickgold.florisboard.util

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.children

fun getColorFromAttr(
    context: Context,
    attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun getBooleanFromAttr(
    context: Context,
    attrBoolean: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Boolean {
    context.theme.resolveAttribute(attrBoolean, typedValue, resolveRefs)
    return typedValue.data != 0
}

fun setBackgroundTintColor(view: View, colorId: Int) {
    view.backgroundTintList = ColorStateList.valueOf(
        getColorFromAttr(view.context, colorId)
    )
}
fun setBackgroundTintColor2(view: View, colorInt: Int) {
    view.backgroundTintList = ColorStateList.valueOf(colorInt)
}
@RequiresApi(Build.VERSION_CODES.M)
fun setDrawableTintColor(view: Button, colorId: Int) {
    view.compoundDrawableTintList = ColorStateList.valueOf(
        getColorFromAttr(view.context, colorId)
    )
}
@RequiresApi(Build.VERSION_CODES.M)
fun setDrawableTintColor2(view: Button, colorInt: Int) {
    view.compoundDrawableTintList = ColorStateList.valueOf(colorInt)
}
fun setImageTintColor2(view: ImageView, colorInt: Int) {
    view.imageTintList = ColorStateList.valueOf(colorInt)
}
@RequiresApi(Build.VERSION_CODES.M)
fun setTextTintColor(view: View, colorId: Int) {
    view.foregroundTintList = ColorStateList.valueOf(
        getColorFromAttr(view.context, colorId)
    )
}
@RequiresApi(Build.VERSION_CODES.M)
fun setTextTintColor2(view: View, colorInt: Int) {
    view.foregroundTintList = ColorStateList.valueOf(colorInt)
}

fun refreshLayoutOf(view: View?) {
    if (view is ViewGroup) {
        view.invalidate()
        view.requestLayout()
        for (childView in view.children) {
            refreshLayoutOf(childView)
        }
    } else {
        view?.invalidate()
        view?.requestLayout()
    }
}
