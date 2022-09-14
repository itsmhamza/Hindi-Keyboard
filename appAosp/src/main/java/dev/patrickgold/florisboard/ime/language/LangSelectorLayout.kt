package dev.patrickgold.florisboard.ime.language

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.core.PrefHelper
import dev.patrickgold.florisboard.util.setUpSpinner
import kotlinx.android.synthetic.main.lang_selector_layout.view.*
import kotlin.math.roundToInt


class LangSelectorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes), FlorisBoard.EventListener,
    AdapterView.OnItemSelectedListener {


    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()
    private val prefs: PrefHelper = PrefHelper.getDefaultInstance(context)

//    private var binding: LangSelectorLayoutBinding = LangSelectorLayoutBinding.bind(this)


    init {
        florisboard?.addEventListener(this)
    }


//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        languageManager = LanguageManager(context)
//        languages = languageManager.fetchLanguages()
//    }

    override fun onWindowShown() {
        super.onWindowShown()
        /**
         * fetch the input and output lang codes for translation.
         */

        setupSpinner()
//        swapBtn.setOnClickListener{
//            inputSpinner.setSelection( prefs.translateLang.outputLangCode)
//            outputSpinner.setSelection(prefs.translateLang.inputLangCode)
//        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                // Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                // Can't be bigger than...
                (florisboard?.inputView?.desiredSmartbarHeight
                    ?: resources.getDimension(R.dimen.lang_selector_baseHeight)).coerceAtMost(
                    heightSize
                )
            }
            else -> {
                // Be whatever you want
                florisboard?.inputView?.desiredSmartbarHeight
                    ?: resources.getDimension(R.dimen.lang_selector_baseHeight)
            }
        }

        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(height.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

    fun setupSpinner() {
        inputSpinner.isEnabled=false


        florisboard?.textInputManager?.languagesList!!.map { it.name }.let {

            inputSpinner.setUpSpinner(
                context,
                it,
                this,
                prefs.translateLang.inputLangCode
            )

            outputSpinner.setUpSpinner(
                context,
                it,
                this,
                prefs.translateLang.outputLangCode
            )
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.inputSpinner -> {
              //  prefs.translateLang.inputLangCode = position
            }
            R.id.outputSpinner -> {
                prefs.translateLang.outputLangCode = position

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

}