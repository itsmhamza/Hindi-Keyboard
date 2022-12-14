/*
 * Copyright (C) 2020 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.core

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.marginTop
import dev.patrickgold.florisboard.BuildConfig
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.language.LangSelectorLayout
import dev.patrickgold.florisboard.ime.text.key.KeyVariation
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.util.ViewLayoutUtils
import dev.patrickgold.florisboard.util.getamilSubtype
import kotlinx.android.synthetic.main.text_translator.view.*
import kotlin.math.roundToInt

/**
 * Root view of the keyboard. Notifies [FlorisBoard] when it has been attached to a window.
 */
class InputView : LinearLayout {

    private lateinit var textViewFlipper: ViewFlipper
    private var florisboard: FlorisBoard = FlorisBoard.getInstance()
    private val prefs: PrefHelper = PrefHelper.getDefaultInstance(context)
    var toogle_remove_text_translator: ImageView? = null
    var et_text_translator: EditText? = null
    var translate_text: ImageView? = null
    var desiredInputViewHeight: Float = resources.getDimension(R.dimen.inputView_baseHeight)
        private set
    var desiredSmartbarHeight: Float = resources.getDimension(R.dimen.smartbar_baseHeight)
        private set
    var desiredTextKeyboardViewHeight: Float =
        resources.getDimension(R.dimen.textKeyboardView_baseHeight)
        private set
    var desiredMediaKeyboardViewHeight: Float =
        resources.getDimension(R.dimen.mediaKeyboardView_baseHeight)
        private set

    var mainViewFlipper: ViewFlipper? = null
        private set
    var langSelectorLayout: LangSelectorLayout? = null
    var oneHandedCtrlPanelStart: LinearLayout? = null
        private set
    var oneHandedCtrlPanelEnd: LinearLayout? = null
    var maindrame: FrameLayout? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        if (BuildConfig.DEBUG) Log.i(this::class.simpleName, "onAttachedToWindow()")

        super.onAttachedToWindow()
        textViewFlipper = findViewById(R.id.text_input_view_flipper)
        mainViewFlipper = findViewById(R.id.main_view_flipper)
        langSelectorLayout = findViewById(R.id.langSelectorLayout)
        oneHandedCtrlPanelStart = findViewById(R.id.one_handed_ctrl_panel_start)
        oneHandedCtrlPanelEnd = findViewById(R.id.one_handed_ctrl_panel_end)
        toogle_remove_text_translator = findViewById(R.id.switch_editor)
        et_text_translator = findViewById(R.id.et_text_translator)
        translate_text = findViewById(R.id.translate_text)
//     maindrame = findViewById(R.id.main_ad_layout)
        florisboard.registerInputView(this)

//        val p = layoutParams.apply {
//            height = 1200
//        }
//        layoutParams = p
    }

    fun changeHeight(isPlus: Boolean) {
        prefs.keyboard.heightFactor = "custom"
        val customHeightFactor = prefs.keyboard.heightFactorCustom
        florisboard.inputView?.prefs?.keyboard?.heightFactorCustom = if (isPlus)
            customHeightFactor.plus(5) else customHeightFactor.minus(5)
        requestLayout()
        invalidate()
    }

    var baseHeight = 0f
    private var baseTextInputHeight = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightFactor = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 1.0f
            else -> if (prefs.keyboard.oneHandedMode != "off") {
                0.9f
            } else {
                1.0f
            }
        } * when (prefs.keyboard.heightFactor) {
            "extra_short" -> 0.85f
            "short" -> 0.90f
            "mid_short" -> 0.95f
            "normal" -> 1.00f
            "mid_tall" -> 1.05f
            "tall" -> 1.10f
            "extra_tall" -> 1.15f
            "custom" -> prefs.keyboard.heightFactorCustom.toFloat() / 100.0f
            else -> 1.00f
        }

        baseHeight = calcInputViewHeight() * heightFactor
        var baseSmartbarHeight = (0.16129f * baseHeight) +
                ViewLayoutUtils.convertDpToPixel(  // adding height and margin of smartbar view + line margin
            16f,
            context
        )

        // var baseSmartbarHeight = 0.4f * baseHeight
        baseTextInputHeight = baseHeight - baseSmartbarHeight
        val tim = florisboard.textInputManager
        val shouldGiveAdditionalSpace = prefs.keyboard.numberRow &&
                !(tim.getActiveKeyboardMode() == KeyboardMode.NUMERIC ||
                        tim.getActiveKeyboardMode() == KeyboardMode.PHONE ||
                        tim.getActiveKeyboardMode() == KeyboardMode.PHONE2)
        if (shouldGiveAdditionalSpace) {
            oneRowHeight()
        }

        /// adding one row height due to extra keys in telugu shift mode
        if (Subtype.DEFAULT == getamilSubtype(true)) {
            oneRowHeight()
        }


        val smartbarDisabled = !prefs.smartbar.enabled ||
                tim.keyVariation == KeyVariation.PASSWORD && prefs.keyboard.numberRow
//                ||
//                tim.getActiveKeyboardMode() == KeyboardMode.NUMERIC
//                tim.getActiveKeyboardMode() == KeyboardMode.PHONE ||
//                tim.getActiveKeyboardMode() == KeyboardMode.PHONE2
        if (smartbarDisabled) {
            baseHeight = baseTextInputHeight
            baseSmartbarHeight = 0.0f

        }
        desiredInputViewHeight = baseHeight
        desiredSmartbarHeight = baseSmartbarHeight
        desiredTextKeyboardViewHeight = baseTextInputHeight
        desiredMediaKeyboardViewHeight = baseHeight

        // 40dp is the height of lang selector layout . due to gone visibility the height cant be get dynamically
        val langSelectorHeight =
            if (langSelectorLayout?.visibility == View.VISIBLE) ViewLayoutUtils.convertDpToPixel(
                40f,
                context
            ).toInt() else 0

        // Add bottom offset for curved screens here. As the desired heights have already been set,
        //  adding a value to the height now will result in a bottom padding (aka offset).
        baseHeight += ViewLayoutUtils.convertDpToPixel(
            florisboard.prefs.keyboard.bottomOffset.toFloat(),
            context
        ) + langSelectorHeight + textViewFlipper.marginTop // adding the lang selector spinner height and margin of keyboard buttons form top


        if (prefs.keyboard.texttranslator == "on") {
            baseHeight += texttranslatorLayout.height
            baseHeight -= desiredSmartbarHeight
        }
//        else
//            baseHeight -= adlayout?.height!!

        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(baseHeight.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

    private fun oneRowHeight() {
        val additionalHeight = desiredTextKeyboardViewHeight * 0.18f
        baseHeight += additionalHeight
        baseTextInputHeight += additionalHeight
    }

    /**
     * Calculates the input view height based on the current screen dimensions and the auto
     * selected dimension values.
     *
     * This method and the fraction values have been inspired by [OpenBoard](https://github.com/dslul/openboard)
     * but are not 1:1 the same. This implementation differs from the
     * [original](https://github.com/dslul/openboard/blob/90ae4c8aec034a8935e1fd02b441be25c7dba6ce/app/src/main/java/org/dslul/openboard/inputmethod/latin/utils/ResourceUtils.java)
     * by calculating the average of the min and max height values, then taking at least the input
     * view base height and return this resulting value.
     */
    private fun calcInputViewHeight(): Float {
        val dm: DisplayMetrics = resources.displayMetrics
        val minBaseSize: Float = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> resources.getFraction(
                R.fraction.inputView_minHeightFraction, dm.heightPixels, dm.heightPixels
            )
            else -> resources.getFraction(
                R.fraction.inputView_minHeightFraction, dm.widthPixels, dm.widthPixels
            )
        }
        val maxBaseSize: Float = resources.getFraction(
            R.fraction.inputView_maxHeightFraction, dm.heightPixels, dm.heightPixels
        )
        return ((minBaseSize + maxBaseSize) / 2.0f).coerceAtLeast(
            resources.getDimension(R.dimen.inputView_baseHeight)
        )
    }

}
