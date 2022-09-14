package dev.patrickgold.florisboard.ime.mic

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieDrawable
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.core.PrefHelper
import kotlinx.android.synthetic.main.mic_input_layout.view.*
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

class MicInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes), FlorisBoard.EventListener {

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()
    private val prefs: PrefHelper = PrefHelper.getDefaultInstance(context)

//    var binding: MicInputLayoutBinding = MicInputLayoutBinding.bind(this)

    private var eventListener: WeakReference<MicEventListener?>? = null


    init {
        florisboard?.addEventListener(this)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        florisboard?.textInputManager?.registerMicView(this)

        onApplyThemeAttributes()
        back.setOnClickListener {
            florisboard?.textInputManager?.textViewGroup?.let {
                florisboard.activeInputMainViewFlipper(
                    it
                )
            }
            eventListener?.get()?.onMicBackClick()
        }
    }

    /**
     * Method from floris event listener.
     */
    override fun onApplyThemeAttributes() {
        lottieanimation!!.setAnimation("mic_animation.json")
        lottieanimation!!.playAnimation()
        lottieanimation!!.repeatCount = LottieDrawable.INFINITE
        //lottieAnimationView!!.loop(true)
//        setBackgroundTintColor2(
//            mainMic, prefs.theme.keyEnterBgColor
//        )
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = florisboard?.inputView?.desiredMediaKeyboardViewHeight ?: 0.0f
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(height.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

    fun setEventListener(listener: MicEventListener) {
        eventListener = WeakReference(listener)
    }


    interface MicEventListener {
        fun onMicClick() {}
        fun onMicBackClick()
    }
}