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

package dev.patrickgold.florisboard.ime.text.smartbar

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.BuildConfig
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.SmartbarBinding
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.core.PrefHelper
import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.suggesstions.SuggestionsAdapter
import dev.patrickgold.florisboard.ime.suggesstions.db.SuggesstionEntity
import dev.patrickgold.florisboard.ime.suggesstions.db.SuggesstionRepo
import dev.patrickgold.florisboard.ime.text.key.KeyVariation
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.math.roundToInt
import android.view.animation.Animation

import android.view.animation.RotateAnimation
import android.animation.PropertyValuesHolder







/**
 * View class which manages the state and the UI of the Smartbar, a key element in the usefulness
 * of FlorisBoard. The view automatically tries to get the current FlorisBoard instance, which it
 * needs to decide when a specific feature component is shown.
 */
class SmartbarView : LinearLayout, FlorisBoard.EventListener {

    var activeEditorText: String? = null

    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()
    private val prefs: PrefHelper = PrefHelper.getDefaultInstance(context)
    private var eventListener: WeakReference<EventListener?>? = null
    private val mainScope = MainScope()

    private var cachedActionStartAreaVisible: Boolean = false

    @IdRes
    private var cachedActionStartAreaId: Int? = null

    @IdRes
    private var cachedMainAreaId: Int? = null
    private var cachedActionEndAreaVisible: Boolean = false

    @IdRes
    private var cachedActionEndAreaId: Int? = null

    var isQuickActionsVisible: Boolean = false
        set(v) {
            binding.quickActionToggle.rotation = if (v) 180.0f else 0.0f
            field = v
        }
    private var shouldSuggestClipboardContents: Boolean = false

    lateinit var binding: SmartbarBinding
    private var indexedActionStartArea: MutableList<Int> = mutableListOf()
    private var indexedMainArea: MutableList<Int> = mutableListOf()
    private var indexedActionEndArea: MutableList<Int> = mutableListOf()

    private var candidateViewList: MutableList<Button> = mutableListOf()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        florisboard?.addEventListener(this)
    }

    /**
     * Called by Android when this view has been attached to a window. At this point we can be
     * certain that all children have been instantiated and that we can begin working with them.
     * After initializing all child views, this method registers the SmartbarView in the
     * TextInputManager, which then starts working together with this view.
     */
    override fun onAttachedToWindow() {
        if (BuildConfig.DEBUG) Log.i(this::class.simpleName, "onAttachedToWindow()")

        super.onAttachedToWindow()

        binding = SmartbarBinding.bind(this)

        /**
        setting up the suggestion adapter
         */
        setupSuggestions()


        for (view in binding.actionStartArea.children) {
            indexedActionStartArea.add(view.id)
        }
        for (view in binding.mainArea.children) {
            indexedMainArea.add(view.id)
        }
        for (view in binding.actionEndArea.children) {
            indexedActionEndArea.add(view.id)
        }

//        candidateViewList.add(binding.candidate0)
//        candidateViewList.add(binding.candidate1)
//        candidateViewList.add(binding.candidate2)

        binding.backButton.setOnClickListener {
            eventListener?.get()?.onSmartbarBackButtonPressed()
        }

        //  Animations SmartBar Views

//        val rotatea = RotateAnimation(
//            0f, 360f,
//            Animation.RELATIVE_TO_SELF, 0.5f,
//            Animation.RELATIVE_TO_SELF, 0.5f
//        )
//
//        rotatea.duration = 1500
//        rotatea.repeatCount = Animation.INFINITE
//        binding.quickActionVoice.startAnimation(rotatea)
//        val rotate = RotateAnimation(
//            0f, 360f,
//            Animation.RELATIVE_TO_SELF, 0.5f,
//            Animation.RELATIVE_TO_SELF, 0.5f
//        )
//
//        rotate.duration = 1500
//        rotate.repeatCount = Animation.INFINITE
//        binding.quickActionMic.startAnimation(rotate)
//
//        val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
//            binding.quickActionTheme,
//            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
//            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
//        )
//        scaleDown.duration = 800
//
//        scaleDown.repeatCount = ObjectAnimator.INFINITE
//        scaleDown.repeatMode = ObjectAnimator.REVERSE
//
//        scaleDown.start()



        binding.clipboardCursorRow.isSmartbarKeyboardView = true
        mainScope.launch(Dispatchers.Default) {
            florisboard?.let {
                val layout = florisboard.textInputManager.layoutManager.fetchComputedLayoutAsync(
                    KeyboardMode.SMARTBAR_CLIPBOARD_CURSOR_ROW,
                    Subtype.DEFAULT,
                    prefs
                ).await()
                launch(Dispatchers.Main) {
                    binding.clipboardCursorRow.computedLayout = layout
                    binding.clipboardCursorRow.updateVisibility()
                }
            }
        }

        binding.clipboardSuggestion.setOnClickListener {
            florisboard?.activeEditorInstance?.performClipboardPaste()
            shouldSuggestClipboardContents = false
            updateSmartbarState()
        }

        binding.numberRow.isSmartbarKeyboardView = true
        mainScope.launch(Dispatchers.Default) {
            florisboard?.let {
                val layout = it.textInputManager.layoutManager.fetchComputedLayoutAsync(
                    KeyboardMode.SMARTBAR_NUMBER_ROW,
                    Subtype.DEFAULT,
                    prefs
                ).await()
                launch(Dispatchers.Main) {
                    binding.numberRow.computedLayout = layout
                    binding.numberRow.updateVisibility()
                }
            }
        }

        // smart bar action click listener
        for (quickAction in binding.quickActions.children) {
            if (quickAction is SmartbarQuickActionButton) {
                quickAction.setOnClickListener {
                    eventListener?.get()?.onSmartbarQuickActionPressed(quickAction.id)
                }
            }
        }

        binding.quickActionToggle.setOnClickListener {
            isQuickActionsVisible = !isQuickActionsVisible
            updateSmartbarState()
        }

        configureFeatureVisibility(
            actionStartAreaVisible = false,
            actionStartAreaId = null,
            mainAreaId = null,
            actionEndAreaVisible = false,
            actionEndAreaId = null
        )

        florisboard?.textInputManager?.registerSmartbarView(this)
    }

    /**
     * Updates the visibility of features based on the provided attributes.
     *
     * @param actionStartAreaVisible True if the action start area should be shown, else false.
     * @param actionStartAreaId The ID of the element to show within the action start area. Set to
     *  null to leave this area blank.
     * @param mainAreaId The ID of the element to show within the main area. Set to null to leave
     *  this area blank.
     * @param actionEndAreaVisible True if the action end area should be shown, else false.
     * @param actionEndAreaId The ID of the element to show within the action end area. Set to null
     *  to leave this area blank.
     */
    private fun configureFeatureVisibility(
        actionStartAreaVisible: Boolean = cachedActionStartAreaVisible,
        @IdRes actionStartAreaId: Int? = cachedActionStartAreaId,
        @IdRes mainAreaId: Int? = cachedMainAreaId,
        actionEndAreaVisible: Boolean = cachedActionEndAreaVisible,
        @IdRes actionEndAreaId: Int? = cachedActionEndAreaId
    ) {
        binding.actionStartArea.visibility = when {
            actionStartAreaVisible && actionStartAreaId != null -> View.GONE
            actionStartAreaVisible && actionStartAreaId == null -> View.GONE
            else -> View.GONE
        }
        if (actionStartAreaId != null) {
            binding.actionStartArea.displayedChild =
                indexedActionStartArea.indexOf(actionStartAreaId).coerceAtLeast(0)
        }
        binding.mainArea.visibility = when (mainAreaId) {
            null -> View.INVISIBLE
            else -> View.VISIBLE
        }
        if (mainAreaId != null) {
            binding.mainArea.displayedChild =
                indexedMainArea.indexOf(mainAreaId).coerceAtLeast(0)
        }
        binding.actionEndArea.visibility = when {
            actionEndAreaVisible && actionEndAreaId != null -> View.VISIBLE
            actionEndAreaVisible && actionEndAreaId == null -> View.INVISIBLE
            else -> View.GONE
        }
        if (actionEndAreaId != null) {
            binding.actionEndArea.displayedChild =
                indexedActionEndArea.indexOf(actionEndAreaId).coerceAtLeast(0)
        }
    }

    /**
     * Updates the Smartbar UI state by looking at the current keyboard mode, key variation, active
     * editor instance, etc. Passes the evaluated attributes to [configureFeatureVisibility].
     */
    fun updateSmartbarState() {
        binding.clipboardCursorRow.updateVisibility()
        when (florisboard) {
            null -> configureFeatureVisibility(
                actionStartAreaVisible = false,
                actionStartAreaId = null,
                mainAreaId = null,
                actionEndAreaVisible = false,
                actionEndAreaId = null
            )
            else -> configureFeatureVisibility(
                actionStartAreaVisible = when (florisboard.textInputManager.keyVariation) {
                    KeyVariation.PASSWORD -> false
                    else -> true
                },
                actionStartAreaId = when (florisboard.textInputManager.getActiveKeyboardMode()) {
                    KeyboardMode.EDITING -> R.id.back_button
                    else -> R.id.quick_action_toggle
                },
                mainAreaId = when (florisboard.textInputManager.keyVariation) {
                    KeyVariation.PASSWORD -> R.id.number_row
                    else -> when (isQuickActionsVisible) {
                        true -> R.id.quick_actions
                        else -> when (florisboard.textInputManager.getActiveKeyboardMode()) {
                            KeyboardMode.EDITING,
                            KeyboardMode.NUMERIC
//                            KeyboardMode.PHONE,
//                            KeyboardMode.PHONE2
                            -> null
                            else -> when {
                                florisboard.activeEditorInstance.isComposingEnabled &&
                                        shouldSuggestClipboardContents
                                -> R.id.clipboard_suggestion_row
                                florisboard.activeEditorInstance.isComposingEnabled &&
                                        florisboard.activeEditorInstance.selection.isCursorMode
                                -> {

                                    R.id.rvSuggestions   // before was R.id.candidates
                                }
                              else -> R.id.quick_actions
                            }
                        }
                    }
                },
                actionEndAreaVisible = when (florisboard.textInputManager.keyVariation) {
                    KeyVariation.PASSWORD -> false
                    else -> true
                },
                actionEndAreaId = null
            )
        }
    }

    private fun setupSuggestions() {

        binding.addSuggestionBtn.setOnClickListener {
            activeEditorText?.let { text ->
                SuggesstionRepo.getInstance(context).insert(SuggesstionEntity(word = text))
//                context.showToast(text)
            }
        }

        suggestionsAdapter =
            SuggestionsAdapter {
                // click callback of recyclerview handle through lambda
                florisboard?.activeEditorInstance?.commitCompletion(
                    it
                )
            }
        binding.rvSuggestions.apply {
            adapterAndManager(
                suggestionsAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>,
                isHorizontal = true
            )
        }
    }

    // before was "onPrimaryClipChanged"
    fun changePrimaryClip() {
        if (prefs.suggestion.enabled && prefs.suggestion.suggestClipboardContent) {
            shouldSuggestClipboardContents = true
            val item = florisboard?.clipboardManager?.primaryClip?.getItemAt(0)
            when {
                item?.text != null -> {
                    binding.clipboardSuggestion.text = item.text
                }
                item?.uri != null -> {
                    binding.clipboardSuggestion.text = "(Image) " + item.uri.toString()
                }
                else -> {
                    binding.clipboardSuggestion.text =
                        item?.text ?: "(Error while retrieving clipboard data)"
                }
            }
            updateSmartbarState()
        }
    }

    fun resetClipboardSuggestion() {
        if (prefs.suggestion.enabled && prefs.suggestion.suggestClipboardContent) {
            shouldSuggestClipboardContents = false
            updateSmartbarState()
        }
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
                    ?: resources.getDimension(R.dimen.smartbar_baseHeight)).coerceAtMost(heightSize)
            }
            else -> {
                // Be whatever you want
                florisboard?.inputView?.desiredSmartbarHeight
                    ?: resources.getDimension(R.dimen.smartbar_baseHeight)
            }
        }

        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(height.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
       // setBackgroundColor(prefs.theme.smartbarBgColor)
        setBackgroundTintColor2(binding.clipboardSuggestion, prefs.theme.smartbarButtonBgColor)
        setDrawableTintColor2(binding.clipboardSuggestion, prefs.theme.smartbarButtonFgColor)
        binding.clipboardSuggestion.setTextColor(prefs.theme.smartbarButtonFgColor)
        for (button in candidateViewList) {
            button.setTextColor(prefs.theme.smartbarFgColor)
        }
    }

    fun setEventListener(listener: EventListener) {
        eventListener = WeakReference(listener)
    }

    /**
    update selection callback called from inputmethod service when selection is updated.
    In this case text is retrived from active editor instance for fetch suggestions from database
    base on last word
     */
    override fun onUpdateSelection() {
//        updateSuggestions()
    }

    private fun updateSuggestions() {
        activeEditorText = florisboard?.activeEditorInstance?.cachedText?.substringAfterLast(" ")
        if (activeEditorText?.isNotEmpty() == true) {
            val suggestiosFromDb =
                SuggesstionRepo.getInstance(context).retrieveFilterSuggesstions(activeEditorText!!)

            suggestionsAdapter.apply {
                // checking if suggestion are empty show the add button to add new suggestion in db
                if (suggestiosFromDb.isEmpty()) {
                    suggestiosFromDb.add(SuggesstionEntity(word = activeEditorText))
                    with(binding.addSuggestionBtn) {
                        if (visibility != View.VISIBLE)
                            showWithReveal()
                    }
                } else {
                    with(binding.addSuggestionBtn) {
                        if (visibility != View.INVISIBLE)
                            hideWithReveal()
                    }
                }

                setSuggestionList(suggestiosFromDb)
                notifyDataSetChanged()
            }

        } else
            suggestionsAdapter.clearlist()
    }

    /**
     * Event listener interface which can be used by other classes to receive updates when something
     * important happens in the Smartbar.
     */
    interface EventListener {
        fun onSmartbarBackButtonPressed() {}

        //fun onSmartbarCandidatePressed() {}
        //fun onSmartbarCandidateLongPressed() {}
        fun onSmartbarQuickActionPressed(@IdRes quickActionId: Int) {}
    }
}
