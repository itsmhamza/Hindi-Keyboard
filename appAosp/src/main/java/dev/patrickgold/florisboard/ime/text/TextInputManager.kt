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

package dev.patrickgold.florisboard.ime.text

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.BuildConfig
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.*
import dev.patrickgold.florisboard.ime.language.LangModel
import dev.patrickgold.florisboard.ime.language.SpinnerLanguageManager
import dev.patrickgold.florisboard.ime.mic.MicInputView
import dev.patrickgold.florisboard.ime.mic.SpeechRecognition
import dev.patrickgold.florisboard.ime.text.editing.EditingKeyboardView
import dev.patrickgold.florisboard.ime.text.gestures.SwipeAction
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.key.KeyData
import dev.patrickgold.florisboard.ime.text.key.KeyType
import dev.patrickgold.florisboard.ime.text.key.KeyVariation
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardView
import dev.patrickgold.florisboard.ime.text.layout.LayoutManager
import dev.patrickgold.florisboard.ime.text.smartbar.SmartbarView
import dev.patrickgold.florisboard.ime.translation.Translation
import dev.patrickgold.florisboard.util.getamilSubtype
import kotlinx.android.synthetic.main.smartbar.view.*
import kotlinx.android.synthetic.main.text_translator.view.*
import kotlinx.coroutines.*
import java.util.*


/**
 * TextInputManager is responsible for managing everything which is related to text input. All of
 * the following count as text input: character, numeric (+advanced), phone and symbol layouts.
 *
 * All of the UI for the different keyboard layouts are kept under the same container element and
 * are separated from media-related UI. The core [FlorisBoard] will pass any event defined in
 * [FlorisBoard.EventListener] through to this class.
 *
 * TextInputManager is also the hub in the communication between the system, the active editor
 * instance and the Smartbar.
 */
class TextInputManager private constructor(val context: Context) : CoroutineScope by MainScope(),
    FlorisBoard.EventListener, SmartbarView.EventListener, MicInputView.MicEventListener {


    var languagesList: List<LangModel>? = null

    private val florisboard = FlorisBoard.getInstance()
    private val activeEditorInstance: EditorInstance
        get() = florisboard.activeEditorInstance

    private var activeKeyboardMode: KeyboardMode? = null
    private val keyboardViews = EnumMap<KeyboardMode, KeyboardView>(KeyboardMode::class.java)
    private var editingKeyboardView: EditingKeyboardView? = null
    private val osHandler = Handler()
    private var textViewFlipper: ViewFlipper? = null
    private val sb = StringBuilder()
    var textViewGroup: LinearLayout? = null
    var micLayout: ConstraintLayout? = null
    lateinit var prefs: PrefHelper
    var keyVariation: KeyVariation = KeyVariation.NORMAL
    val layoutManager = LayoutManager(florisboard)

    private var smartbarView: SmartbarView? = null
    private var subtype: SubtypeManager? = null
    private var micView: MicInputView? = null

    // Caps/Space related properties
    var caps: Boolean = false
        private set
    var capsLock: Boolean = false
        private set
    private var hasCapsRecentlyChanged: Boolean = false
    private var hasSpaceRecentlyPressed: Boolean = false

    // Composing text related properties
    var isManualSelectionMode: Boolean = false
    private var isManualSelectionModeLeft: Boolean = false
    private var isManualSelectionModeRight: Boolean = false
    var speechRecognition: SpeechRecognition? = null

    companion object {
        private val TAG: String? = TextInputManager::class.simpleName
        private var instance: TextInputManager? = null

        @Synchronized
        fun getInstance(context: Context): TextInputManager {
            if (instance == null) {
                instance = TextInputManager(context)
            }
            return instance!!
        }
    }

    init {
        florisboard.addEventListener(this)
    }


    /**
     * init the speech recognition for voice input.
     */
    private fun initSpeechRecognition() {

        speechRecognition = object : SpeechRecognition(context, prefs) {


            override fun onResults(results: Bundle?) {
                speechRecognition?.getResultFromBundle(
                    results,
                    florisboard.prefs.keyboard.translation,
                    SpinnerLanguageManager.getInputLangCode(florisboard.prefs),
                    SpinnerLanguageManager.getOutputLangCode(florisboard.prefs)
                ) {
                    activeEditorInstance.commitText(it)
                }
//                florisboard.setActiveInput(R.id.text_input)
            //    smartbarView?.binding?.quickActionMic?.setBackgroundResource(R.drawable.oval_red)

            }

            override fun onEndOfSpeech() {
                speechRecognition?.stopListen()
//                florisboard.setActiveInput(R.id.text_input)
            //    smartbarView?.binding?.quickActionMic?.setBackgroundResource(R.drawable.oval_red)

                smartbarView
            }

            override fun onError(error: Int) {
                speechRecognition?.stopListen()
//                florisboard.setActiveInput(R.id.text_input)
            //    smartbarView?.binding?.quickActionMic?.setBackgroundResource(R.drawable.oval_red)

            }
        }
    }

    /**
     * Non-UI-related setup + preloading of all required computed layouts (asynchronous in the
     * background).
     */
    override fun onCreate() {
        if (BuildConfig.DEBUG) Log.i(TAG, "onCreate()")
        prefs = PrefHelper.getDefaultInstance(FlorisBoard.getInstance())
        prefs.initDefaultPreferences()
        prefs.sync()
        var subtypes = florisboard.subtypeManager.subtypes
        if (subtypes.isEmpty()) {
            subtypes = listOf(Subtype.DEFAULT)
        }
        for (subtype in subtypes) {
            for (mode in KeyboardMode.values()) {
                layoutManager.preloadComputedLayout(mode, subtype, florisboard.prefs)
            }
        }

        languagesList = SpinnerLanguageManager.getLangList(context)

        // init speech recognition
        initSpeechRecognition()


    }

    private suspend fun addKeyboardView(mode: KeyboardMode) {
        val keyboardView = KeyboardView(florisboard.context)
        keyboardView.computedLayout = layoutManager.fetchComputedLayoutAsync(
            mode,
            florisboard.activeSubtype,
            florisboard.prefs
        ).await()
        keyboardViews[mode] = keyboardView
        withContext(Dispatchers.Main) {
            textViewFlipper?.addView(keyboardView)
        }
    }

    /**
     * Sets up the newly registered input view.
     */
    override fun onRegisterInputView(inputView: InputView) {
        if (BuildConfig.DEBUG) Log.i(TAG, "onRegisterInputView(inputView)")

//        context.registerReceiver(InternetReciever(), getConnectivityFilter())

        launch(Dispatchers.Default) {
            micLayout = inputView.findViewById(R.id.mic_input)
            textViewGroup = inputView.findViewById(R.id.text_input)
            textViewFlipper = inputView.findViewById(R.id.text_input_view_flipper)
            editingKeyboardView = inputView.findViewById(R.id.editing)

            val activeKeyboardMode = getActiveKeyboardMode()
            addKeyboardView(activeKeyboardMode)
            withContext(Dispatchers.Main) {
                setActiveKeyboardMode(activeKeyboardMode)
            }
            for (mode in KeyboardMode.values()) {
                if (mode != activeKeyboardMode && mode != KeyboardMode.SMARTBAR_NUMBER_ROW) {
                    addKeyboardView(mode)
                }
            }
        }
    }

    fun registerSmartbarView(view: SmartbarView) {
        smartbarView = view
        smartbarView?.setEventListener(this)
    }

    fun registerMicView(micInputView: MicInputView) {
        micInputView.also {
            micView = it
            it.setEventListener(this)
        }
    }

    /**
     * Cancels all coroutines and cleans up.
     */
    override fun onDestroy() {
        if (BuildConfig.DEBUG) Log.i(TAG, "onDestroy()")

        cancel()
        osHandler.removeCallbacksAndMessages(null)
        layoutManager.onDestroy()
        instance = null
        speechRecognition = null
    }

    /**
     * Evaluates the [activeKeyboardMode], [keyVariation] and [EditorInstance.isComposingEnabled]
     * property values when starting to interact with a input editor. Also resets the composing
     * texts and sets the initial caps mode accordingly.
     */
    override fun onStartInputView(instance: EditorInstance, restarting: Boolean) {

        val keyboardMode = when (instance.inputAttributes.type) {
            InputAttributes.Type.NUMBER -> {
                keyVariation = KeyVariation.NORMAL
                KeyboardMode.NUMERIC
            }
            InputAttributes.Type.PHONE -> {
                keyVariation = KeyVariation.NORMAL
                KeyboardMode.PHONE
            }
            InputAttributes.Type.TEXT -> {
                keyVariation = when (instance.inputAttributes.variation) {
                    InputAttributes.Variation.EMAIL_ADDRESS,
                    InputAttributes.Variation.WEB_EMAIL_ADDRESS -> {
                        KeyVariation.EMAIL_ADDRESS
                    }
                    InputAttributes.Variation.PASSWORD,
                    InputAttributes.Variation.VISIBLE_PASSWORD,
                    InputAttributes.Variation.WEB_PASSWORD -> {
                        KeyVariation.PASSWORD
                    }
                    InputAttributes.Variation.URI -> {
                        KeyVariation.URI
                    }
                    else -> {
                        KeyVariation.NORMAL
                    }
                }
                KeyboardMode.CHARACTERS
            }
            else -> {
                keyVariation = KeyVariation.NORMAL
                KeyboardMode.CHARACTERS
            }
        }
        instance.isComposingEnabled = when (keyboardMode) {
            KeyboardMode.NUMERIC,
            KeyboardMode.PHONE,
            KeyboardMode.PHONE2 -> false
            else -> keyVariation != KeyVariation.PASSWORD &&
                    florisboard.prefs.suggestion.enabled// &&
            //!instance.inputAttributes.flagTextAutoComplete &&
            //!instance.inputAttributes.flagTextNoSuggestions
        }
        if (!florisboard.prefs.correction.rememberCapsLockState) {
            capsLock = false
        }
        updateCapsState()
        setActiveKeyboardMode(keyboardMode)
        smartbarView?.updateSmartbarState()
    }

    /**
     * Handle stuff when finishing to interact with a input editor.
     */
    override fun onFinishInputView(finishingInput: Boolean) {
        smartbarView?.updateSmartbarState()
    }

    override fun onWindowShown() {
        keyboardViews[KeyboardMode.CHARACTERS]?.updateVisibility()
        smartbarView?.updateSmartbarState()

    }


    /**
     * Gets [activeKeyboardMode].
     *
     * @return If null [KeyboardMode.CHARACTERS], else [activeKeyboardMode].
     */
    fun getActiveKeyboardMode(): KeyboardMode {
        return activeKeyboardMode ?: KeyboardMode.CHARACTERS
    }

    /**
     * Sets [activeKeyboardMode] and updates the [SmartbarView.isQuickActionsVisible] state.
     */
    private fun setActiveKeyboardMode(mode: KeyboardMode) {
        textViewFlipper?.displayedChild = textViewFlipper?.indexOfChild(
            when (mode) {
                KeyboardMode.EDITING -> editingKeyboardView
                else -> keyboardViews[mode]
            }
        ) ?: 0

        keyboardViews[mode]?.apply {
            updateVisibility()
            requestLayout()
            requestLayoutAllKeys()
        }
        activeKeyboardMode = mode
        isManualSelectionMode = false
        isManualSelectionModeLeft = false
        isManualSelectionModeRight = false
        smartbarView?.isQuickActionsVisible = false
        smartbarView?.updateSmartbarState()
    }

    override fun onSubtypeChanged(newSubtype: Subtype) {
        launch {
            val keyboardView = keyboardViews[KeyboardMode.CHARACTERS]
            keyboardView?.computedLayout = layoutManager.fetchComputedLayoutAsync(
                KeyboardMode.CHARACTERS,
                newSubtype,
                florisboard.prefs
            ).await()
            keyboardView?.updateVisibility()
        }
    }

    /**
     * Main logic point for processing cursor updates as well as parsing the current composing word
     * and passing this info on to the [SmartbarView] to turn it into candidate suggestions.
     */
    override fun onUpdateSelection() {
        if (!activeEditorInstance.isNewSelectionInBoundsOfOld) {
            isManualSelectionMode = false
            isManualSelectionModeLeft = false
            isManualSelectionModeRight = false
        }
        updateCapsState()
        smartbarView?.updateSmartbarState()
    }

    override fun onPrimaryClipChanged() {
        smartbarView?.changePrimaryClip()
    }

    /**
     * Updates the current caps state according to the [EditorInstance.cursorCapsMode], while
     * respecting [capsLock] property and the correction.autoCapitalization preference.
     */
    private fun updateCapsState() {
        if (!capsLock) {
            caps = florisboard.prefs.correction.autoCapitalization &&
                    activeEditorInstance.cursorCapsMode != InputAttributes.CapsMode.NONE
            launch(Dispatchers.Main) {
                keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
            }
        }
    }

    /**
     * Executes a given [SwipeAction]. Ignores any [SwipeAction] but the ones relevant for this
     * class.
     */
    fun executeSwipeAction(swipeAction: SwipeAction) {
        when (swipeAction) {
            SwipeAction.DELETE_WORD -> handleDeleteWord()
            SwipeAction.MOVE_CURSOR_DOWN -> handleArrow(KeyCode.ARROW_DOWN)
            SwipeAction.MOVE_CURSOR_UP -> handleArrow(KeyCode.ARROW_UP)
            SwipeAction.MOVE_CURSOR_LEFT -> handleArrow(KeyCode.ARROW_LEFT)
            SwipeAction.MOVE_CURSOR_RIGHT -> handleArrow(KeyCode.ARROW_RIGHT)
            SwipeAction.SHIFT -> handleShiftKey() //handleShift()
            else -> {
            }
        }
    }

    override fun onSmartbarBackButtonPressed() {
        setActiveKeyboardMode(KeyboardMode.CHARACTERS)
    }

    override fun onSmartbarQuickActionPressed(quickActionId: Int) {
        when (quickActionId) {
//            R.id.quick_action_switch_to_editing_context -> {
//                if (activeKeyboardMode == KeyboardMode.EDITING) {
//                    setActiveKeyboardMode(KeyboardMode.CHARACTERS)
//                } else {
//                    setActiveKeyboardMode(KeyboardMode.EDITING)
//                }
//            }
            R.id.quick_action_switch_to_media_context ->

                changeKeyboard()

            R.id.quick_action_text_translation ->
                textTranslationSwitchOn()


            R.id.quick_action_switch_keyboard ->
                changelanguage()
            //    florisboard.launchSettings()

            //   R.id.quick_action_one_handed_toggle -> florisboard.toggleOneHandedMode()
            R.id.quick_action_mic -> {

                speechRecognition?.listenSpeech {
                    // lambda for handling extra functionality before invoking the listener
//                    florisboard.setActiveInput(R.id.mic_input)
         //           smartbarView?.binding?.quickActionMic?.setBackgroundResource(R.drawable.oval_green)
                }

            }

            R.id.quick_action_settings -> {
                try {
                    val intent = Intent(
                        context,
                        Class.forName("com.example.speaktranslate.TamilKeyboardActivity")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            R.id.quick_action_theme -> {
                try {
                    val intent = Intent(
                        context,
                        Class.forName("com.example.speaktranslate.ThemesActivity")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            R.id.quick_action_voice_typing -> {
                try {
                    val intent = Intent(
                        context,
                        Class.forName("com.example.speaktranslate.VoiceTyping")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            R.id.quick_action_voice_translator -> {
                try {
                    val intent = Intent(
                        context,
                        Class.forName("com.example.speaktranslate.TranslationActivity")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            R.id.quick_action_voice -> {
                try {
                    val intent = Intent(
                        context,
                        Class.forName("com.example.speaktranslate.MainActivity")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
//            R.id.quick_action_translation_lang -> {
//                val layout = florisboard.inputView?.langSelectorLayout
//                if (layout?.visibility == View.VISIBLE)
//                    layout.visibility = View.GONE
//                else
//                    layout?.visibility = View.VISIBLE
//            }
        }
        smartbarView?.isQuickActionsVisible = true
        smartbarView?.updateSmartbarState()
    }


    override fun onMicBackClick() {
        speechRecognition?.stopListen()
    }

    /**
     * Handles a [KeyCode.DELETE] event.
     */
    private fun handleDelete() {
        isManualSelectionMode = false
        isManualSelectionModeLeft = false
        isManualSelectionModeRight = false
        activeEditorInstance.deleteBackwards()
        smartbarView?.resetClipboardSuggestion()
    }

    /**
     * Handles a [KeyCode.DELETE_WORD] event.
     */
    private fun handleDeleteWord() {
        isManualSelectionMode = false
        isManualSelectionModeLeft = false
        isManualSelectionModeRight = false
        activeEditorInstance.deleteWordsBeforeCursor(1)
    }

    // WHEN TEXT TRANSLATION IS ON AND EDITTEXT OF TEXT TRANSLATOR ENABLE
    private fun textTranslationSwitchOn() {
        if (Subtype.DEFAULT == Subtype(-1, Locale.ENGLISH, "qwerty")) {
            changeCaps(false)
            changeDefaultKeyboardJson(-1, Locale.ENGLISH, "telugu")
        }
//        florisboard.inputView?.et_text_translator?.requestFocus()

        florisboard.inputView?.et_text_translator?.setOnClickListener {
            florisboard.inputView?.et_text_translator?.requestFocus()
        }

        florisboard.inputView?.et_text_translator?.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {

//                    florisboard.inputView?.et_text_translator?.requestFocus()
                    florisboard.inputView?.et_text_translator?.isCursorVisible = true
                    v.invalidate()
                }
                // do something when edit text get focus
                else {
//                    florisboard.inputView?.et_text_translator?.clearFocus()
                }

            }
        prefs.keyboard.texttranslator = "on"

        florisboard.inputView?.apply {
            langSelectorLayout?.visibility = View.VISIBLE
            texttranslatorLayout?.visibility = View.VISIBLE
            smartbar?.visibility = View.GONE
            florisboard.inputView?.et_text_translator?.setSelection(florisboard.inputView?.et_text_translator?.length()!!)

            florisboard.textInputManager.onRegisterInputView(this)
        }

    }

    // WHEN TEXT TRANSLATION IS OFF
    fun textTranslationSwitchOff() {
        prefs.keyboard.texttranslator = "off"
        florisboard.inputView?.apply {
            langSelectorLayout?.visibility = View.GONE
            texttranslatorLayout?.visibility = View.GONE
            smartbar?.visibility = View.VISIBLE
            florisboard.inputView?.et_text_translator?.text?.clear()
//            florisboard.inputView?.et_text_translator?.clearFocus()
        }

        sb.clear()
        florisboard.inputView?.let { florisboard.textInputManager.onRegisterInputView(it) }
    }

    //WHEN TEXT TRANSLATION GET VALUE FROM EDITOR AND COMMIT TRANSLATION TEXT IN EDITORINSTANCE OBJECT
    fun translateTextCommit() {
        florisboard.inputView?.et_text_translator?.requestFocus()

        if (florisboard.inputView?.et_text_translator?.text!!.isNotEmpty()) {
            florisboard.textInputManager.translateText(
                florisboard.inputView?.et_text_translator?.text?.toString(),
                SpinnerLanguageManager.getInputLangCode(florisboard.prefs),
                SpinnerLanguageManager.getOutputLangCode(florisboard.prefs)
            )
            florisboard.inputView?.et_text_translator?.text?.clear()
            sb.clear()
        } else {
            Toast.makeText(
                FlorisBoard.getInstance(),
                "No Word Found For Translation",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * Handles a [KeyCode.ENTER] event.
     */
    private fun handleEnter() {
        if (activeEditorInstance.imeOptions.flagNoEnterAction) {
            activeEditorInstance.performEnter()
        } else {
            when (activeEditorInstance.imeOptions.action) {
                ImeOptions.Action.DONE,
                ImeOptions.Action.GO,
                ImeOptions.Action.NEXT,
                ImeOptions.Action.PREVIOUS,
                ImeOptions.Action.SEARCH,
                ImeOptions.Action.SEND -> {
                    activeEditorInstance.performEnterAction(activeEditorInstance.imeOptions.action)
                }
                else -> activeEditorInstance.performEnter()
            }
        }
    }

    /**
     * Handles a [KeyCode.SHIFT] event.
     */
    private fun handleShift() {

        if (hasCapsRecentlyChanged) {
            osHandler.removeCallbacksAndMessages(null)
            caps = true
            capsLock = true
            hasCapsRecentlyChanged = false

        } else {
            caps = !caps
            capsLock = false
            hasCapsRecentlyChanged = true
            osHandler.postDelayed({
                hasCapsRecentlyChanged = false
            }, 300)
        }
        keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
    }

    /**
     * Handles a [KeyCode.SPACE] event. Also handles the auto-correction of two space taps if
     * enabled by the user.
     */
    private fun handleSpace() {
        if (prefs.keyboard.texttranslator == "on") {
            sb.append(KeyCode.SPACE.toChar().toString())
            florisboard.inputView?.et_text_translator?.setText(sb)
            florisboard.inputView?.et_text_translator?.setSelection(florisboard.inputView?.et_text_translator?.length()!!)
        } else {
            if (florisboard.prefs.correction.doubleSpacePeriod) {
                if (hasSpaceRecentlyPressed) {
                    osHandler.removeCallbacksAndMessages(null)
                    val text = activeEditorInstance.getTextBeforeCursor(2)
                    if (text.length == 2 && !text.matches("""[.!?‽\s][\s]""".toRegex())) {
                        activeEditorInstance.deleteBackwards()
                        activeEditorInstance.commitText(".")
                    }
                    hasSpaceRecentlyPressed = false
                } else {
                    hasSpaceRecentlyPressed = true
                    osHandler.postDelayed({
                        hasSpaceRecentlyPressed = false
                    }, 300)
                }
            }

            activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())
        }
        //  activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())
    }

    /**
     * Handles [KeyCode] arrow and move events, behaves differently depending on text selection.
     */
    private fun handleArrow(code: Int) = activeEditorInstance.apply {
        val selectionStartMin = 0
        val selectionEndMax = cachedText.length
        if (selection.isSelectionMode && isManualSelectionMode) {
            // Text is selected and it is manual selection -> Expand selection depending on started
            //  direction.
            when (code) {
                KeyCode.ARROW_DOWN -> {
                }
                KeyCode.ARROW_LEFT -> {
                    if (isManualSelectionModeLeft) {
                        setSelection(
                            (selection.start - 1).coerceAtLeast(selectionStartMin),
                            selection.end
                        )
                    } else {
                        setSelection(selection.start, selection.end - 1)
                    }
                }
                KeyCode.ARROW_RIGHT -> {
                    if (isManualSelectionModeRight) {
                        setSelection(
                            selection.start,
                            (selection.end + 1).coerceAtMost(selectionEndMax)
                        )
                    } else {
                        setSelection(selection.start + 1, selection.end)
                    }
                }
                KeyCode.ARROW_UP -> {
                }
                KeyCode.MOVE_HOME -> {
                    if (isManualSelectionModeLeft) {
                        setSelection(selectionStartMin, selection.end)
                    } else {
                        setSelection(selectionStartMin, selection.start)
                    }
                }
                KeyCode.MOVE_END -> {
                    if (isManualSelectionModeRight) {
                        setSelection(selection.start, selectionEndMax)
                    } else {
                        setSelection(selection.end, selectionEndMax)
                    }
                }
            }
        } else if (selection.isSelectionMode && !isManualSelectionMode) {
            // Text is selected but no manual selection mode -> arrows behave as if selection was
            //  started in manual left mode
            when (code) {
                KeyCode.ARROW_DOWN -> {
                }
                KeyCode.ARROW_LEFT -> {
                    setSelection(selection.start, selection.end - 1)
                }
                KeyCode.ARROW_RIGHT -> {
                    setSelection(
                        selection.start,
                        (selection.end + 1).coerceAtMost(selectionEndMax)
                    )
                }
                KeyCode.ARROW_UP -> {
                }
                KeyCode.MOVE_HOME -> {
                    setSelection(selectionStartMin, selection.start)
                }
                KeyCode.MOVE_END -> {
                    setSelection(selection.start, selectionEndMax)
                }
            }
        } else if (!selection.isSelectionMode && isManualSelectionMode) {
            // No text is selected but manual selection mode is active, user wants to start a new
            //  selection. Must set manual selection direction.
            when (code) {
                KeyCode.ARROW_DOWN -> {
                }
                KeyCode.ARROW_LEFT -> {
                    setSelection(
                        (selection.start - 1).coerceAtLeast(selectionStartMin),
                        selection.start
                    )
                    isManualSelectionModeLeft = true
                    isManualSelectionModeRight = false
                }
                KeyCode.ARROW_RIGHT -> {
                    setSelection(
                        selection.end,
                        (selection.end + 1).coerceAtMost(selectionEndMax)
                    )
                    isManualSelectionModeLeft = false
                    isManualSelectionModeRight = true
                }
                KeyCode.ARROW_UP -> {
                }
                KeyCode.MOVE_HOME -> {
                    setSelection(selectionStartMin, selection.start)
                    isManualSelectionModeLeft = true
                    isManualSelectionModeRight = false
                }
                KeyCode.MOVE_END -> {
                    setSelection(selection.end, selectionEndMax)
                    isManualSelectionModeLeft = false
                    isManualSelectionModeRight = true
                }
            }
        } else {
            // No selection and no manual selection mode -> move cursor around
            when (code) {
                KeyCode.ARROW_DOWN -> activeEditorInstance.sendSystemKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN)
                KeyCode.ARROW_LEFT -> activeEditorInstance.sendSystemKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT)
                KeyCode.ARROW_RIGHT -> activeEditorInstance.sendSystemKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT)
                KeyCode.ARROW_UP -> activeEditorInstance.sendSystemKeyEvent(KeyEvent.KEYCODE_DPAD_UP)
                KeyCode.MOVE_HOME -> activeEditorInstance.sendSystemKeyEventAlt(KeyEvent.KEYCODE_DPAD_UP)
                KeyCode.MOVE_END -> activeEditorInstance.sendSystemKeyEventAlt(KeyEvent.KEYCODE_DPAD_DOWN)
            }
        }
    }

    /**
     * Handles a [KeyCode.CLIPBOARD_SELECT] event.
     */
    private fun handleClipboardSelect() = activeEditorInstance.apply {
        if (selection.isSelectionMode) {
            if (isManualSelectionMode && isManualSelectionModeLeft) {
                setSelection(selection.start, selection.start)
            } else {
                setSelection(selection.end, selection.end)
            }
            isManualSelectionMode = false
        } else {
            isManualSelectionMode = !isManualSelectionMode
            // Must call to update UI properly
            editingKeyboardView?.onUpdateSelection()
        }
    }

    /**
     * Handles a [KeyCode.CLIPBOARD_SELECT_ALL] event.
     */
    private fun handleClipboardSelectAll() {
        activeEditorInstance.setSelection(0, activeEditorInstance.cachedText.length)
    }

    /**
     * Main logic point for sending a key press. Different actions may occur depending on the given
     * [KeyData]. This method handles all key press send events, which are text based. For media
     * input send events see MediaInputManager.
     *
     * @param keyData The [KeyData] object which should be sent.
     */
    fun sendKeyPress(keyData: KeyData) {
        when (keyData.code) {
            KeyCode.INCREASE -> {
                florisboard.inputView?.changeHeight(isPlus = true)
            }
            KeyCode.DECREASE -> {
                florisboard.inputView?.changeHeight(isPlus = false)
            }
            KeyCode.ARROW_DOWN,
            KeyCode.ARROW_LEFT,
            KeyCode.ARROW_RIGHT,
            KeyCode.ARROW_UP,
            KeyCode.MOVE_HOME,
            KeyCode.MOVE_END -> handleArrow(keyData.code)
            KeyCode.CLIPBOARD_CUT -> activeEditorInstance.performClipboardCut()
            KeyCode.CLIPBOARD_COPY -> activeEditorInstance.performClipboardCopy()
            KeyCode.CLIPBOARD_PASTE -> {
                activeEditorInstance.performClipboardPaste()
                smartbarView?.resetClipboardSuggestion()
            }
            KeyCode.CLIPBOARD_SELECT -> handleClipboardSelect()
            KeyCode.CLIPBOARD_SELECT_ALL -> handleClipboardSelectAll()
            KeyCode.DELETE -> {
                if (prefs.keyboard.texttranslator == "on") {
//                    launch(Dispatchers.Main) {
//
//                    }
                    handleInnerEtDelete()

                } else {
                    handleDelete()

                }
            }
            KeyCode.ENTER -> {
                handleEnter()
                smartbarView?.resetClipboardSuggestion()
            }
            KeyCode.LANGUAGE_SWITCH -> florisboard.switchToNextSubtype()
            KeyCode.SETTINGS -> florisboard.launchSettings()
            KeyCode.SHIFT ->
                handleShiftKey()
//            handleShift()
            KeyCode.SHOW_INPUT_METHOD_PICKER -> {
                val im =
                    florisboard.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.showInputMethodPicker()
            }
            KeyCode.TOGGLE_TRANSLETRATION -> florisboard.toggleTransletratiOnOff()
            KeyCode.SWITCH_TO_MEDIA_CONTEXT -> florisboard.setActiveInput(R.id.media_input)
            KeyCode.SWITCH_TO_TEXT_CONTEXT -> florisboard.setActiveInput(R.id.text_input)
            KeyCode.TOGGLE_ONE_HANDED_MODE -> florisboard.toggleOneHandedMode()
            KeyCode.VIEW_CHARACTERS -> {
                changeCaps(false)
                setActiveKeyboardMode(KeyboardMode.CHARACTERS)
            }
            KeyCode.VIEW_NUMERIC -> setActiveKeyboardMode(KeyboardMode.NUMERIC)
            KeyCode.VIEW_NUMERIC_ADVANCED -> setActiveKeyboardMode(KeyboardMode.NUMERIC_ADVANCED)
            KeyCode.VIEW_PHONE -> setActiveKeyboardMode(KeyboardMode.PHONE)
            KeyCode.VIEW_PHONE2 -> setActiveKeyboardMode(KeyboardMode.PHONE2)
            KeyCode.VIEW_SYMBOLS ->
                setDefaultKeyboard(0)
            KeyCode.VIEW_SYMBOLS2 ->
                setDefaultKeyboard(1)
            //  setActiveKeyboardMode(KeyboardMode.SYMBOLS2)
            else -> {
                when (activeKeyboardMode) {
                    KeyboardMode.NUMERIC,
                    KeyboardMode.NUMERIC_ADVANCED,
                    KeyboardMode.PHONE,
                    KeyboardMode.PHONE2 -> when (keyData.type) {
                        KeyType.CHARACTER,
                        KeyType.NUMERIC -> {
                            val text = keyData.code.toChar().toString()
                            activeEditorInstance.commitText(text)
                        }
                        else -> when (keyData.code) {
                            KeyCode.PHONE_PAUSE,
                            KeyCode.PHONE_WAIT -> {
                                val text = keyData.code.toChar().toString()
                                activeEditorInstance.commitText(text)
                            }
                        }
                    }
                    else -> when (keyData.type) {
                        KeyType.CHARACTER ->
                            when (keyData.code) {
                                KeyCode.TAMIL_KEY_2 -> {

                                    // handling for one long word in last row
//
                                    activeEditorInstance.commitText("க்ஷ")
                                }
                                KeyCode.SPACE ->

                                    if (Subtype.DEFAULT == getamilSubtype()
                                        || Subtype.DEFAULT == getamilSubtype(true)
                                    ) {
                                        handleSpace()
                                    } else {
                                        if (prefs.keyboard.transletration == "on" && prefs.keyboard.texttranslator == "off")
                                            handleSpaceTransletationOn()
                                        else
                                            handleSpace()
                                    }

                                KeyCode.URI_COMPONENT_TLD -> {
                                    val tld = when (caps) {
                                        true -> keyData.label.toUpperCase(Locale.getDefault())
                                        false -> keyData.label.toLowerCase(Locale.getDefault())
                                    }
                                    activeEditorInstance.commitText(tld)
                                }
                                else -> {
                                    var text = keyData.code.toChar().toString()
                                    text = when (caps) {
                                        true -> text.toUpperCase(Locale.getDefault())
                                        false -> text.toLowerCase(Locale.getDefault())
                                    }
                                    if (prefs.keyboard.texttranslator == "on") {
                                        sb.append(text)
                                        florisboard.inputView?.et_text_translator?.setText(sb)
                                        florisboard.inputView?.et_text_translator?.setSelection(
                                            florisboard.inputView?.et_text_translator?.length()!!
                                        )
                                    } else {
                                        activeEditorInstance.commitText(text)
                                    }

                                    //activeEditorInstance.commitText(text)
                                }
                            }
                        else -> {
                            Log.e(TAG, "sendKeyPress(keyData): Received unknown key: $keyData")
                        }
                    }
                }
                smartbarView?.resetClipboardSuggestion()
            }
        }
        if (keyData.code != KeyCode.SHIFT && !capsLock) {
            updateCapsState()
        }
        smartbarView?.updateSmartbarState()
    }

    private fun handleInnerEtDelete() {
        val etTranslate = florisboard.inputView?.et_text_translator
        val start: Int =
            etTranslate?.selectionStart!!
        val end: Int =
            etTranslate.selectionEnd

        val length = end - start // text selection length difference
        when {
            length > 0 -> etTranslate.text  // if length is greater than zero then text is selected
                ?.delete(start, end).toString()

            start > 0 -> etTranslate.text       // normal curson position of edittext
                ?.delete(start - 1, end).toString()

            etTranslate.text.isEmpty() -> {
                florisboard.inputView?.et_text_translator?.clearFocus()
                handleDelete()
            }
        }

        //                        if (length > 0)
        //                        else if (start > 0)

        sb.apply {
            clear()
            append(florisboard.inputView?.et_text_translator?.text)
        }
    }

    //  This Method is use to Transletration On and Translate the word After Space  ---->Truckers
    private fun handleSpaceTransletationOn() {

        this.osHandler.removeCallbacksAndMessages(null)
        val text = activeEditorInstance.getTextBeforeCursor(100)
        val segments: List<String> = text.split(" ")
        if (text.trim().isEmpty()) {

            activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())

            //   activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())
        } else {
            val lastInputSegment = segments[segments.size - 1]
            if (lastInputSegment != "") {
                translateText(lastInputSegment, "en", "ta")
                activeEditorInstance.deleteWordsBeforeCursor(lastInputSegment.length)
            }
        }
    }


    fun translateText(query: String?, inputCode: String?, outputCode: String?) {
        val translation = Translation(FlorisBoard.getInstance())
        translation.runTranslation(query!!, outputCode!!, inputCode!!)
        translation.setTranslationComplete(object : Translation.TranslationComplete {
            override fun translationCompleted(translation: String, language: String) {

                activeEditorInstance.commitCompletion("$translation ")
            }
        })
    }

    private fun handleSpaceTransletrationOFF() {

        if (florisboard.prefs.correction.doubleSpacePeriod) {
            if (hasSpaceRecentlyPressed) {
                osHandler.removeCallbacksAndMessages(null)
                val text = activeEditorInstance.getTextBeforeCursor(2)
                if (text.length == 2 && !text.matches("""[.!?‽\s][\s]""".toRegex())) {
                    activeEditorInstance.deleteBackwards()
                    activeEditorInstance.commitText(".")
                }
                hasSpaceRecentlyPressed = false
            } else {
                hasSpaceRecentlyPressed = true
                osHandler.postDelayed({
                    hasSpaceRecentlyPressed = false
                }, 300)
            }
        } else {
            activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())
        }
    }

    //This Method is use to Switch language     ---->Truckers
    fun changelanguage() {

        if (Subtype.DEFAULT == Subtype(-1, Locale.ENGLISH, "qwerty")) {
            changeCaps(false)
            changeDefaultKeyboardJson(-1, Locale.ENGLISH, "hindi")
        } else {
            changeDefaultKeyboardJson(-1, Locale.ENGLISH, "qwerty")
        }

        if (activeKeyboardMode != KeyboardMode.CHARACTERS)
            setActiveKeyboardMode(KeyboardMode.CHARACTERS)
    }
    // This Method is use to Shift Key Press Get more Default language Keys or Undo


    fun handleShiftKey() {

        if (Subtype.DEFAULT == Subtype(-1, Locale.ENGLISH, "qwerty")) {
            handleShift()

        }
//        else {
//
//            if (Subtype.DEFAULT == getamilSubtype()) {
//                changeDefaultKeyboardJson(-1, Locale.ENGLISH, "telugushift")
//                changeCaps(true)
//
//            } else {
//                changeDefaultKeyboardJson(-1, Locale.ENGLISH, "telugu")
//                changeCaps(false)
//            }
//
////            keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
////            handleShift()
//        }

//        onCreateInputView()

    }

    private fun changeCaps(state: Boolean) {
        caps = state
        capsLock = state
    }

    //This Method is use to Change Subtype to Default on Keyboard
    fun changeDefaultKeyboardJson(
        id: Int,
        locale: Locale,
        layout: String
    ) {

        Subtype.DEFAULT = Subtype(id, locale, layout)
        florisboard.activeSubtype =
            florisboard.subtypeManager.switchToNextSubtype() ?: Subtype.DEFAULT
        onSubtypeChanged(florisboard.activeSubtype)
    }

    //This Method is use to Set Default Language On Keyboard Note: For Emoji Switch maintain the UI and Size of the Keyboard Height   ---->Truckers
    fun changeKeyboard() {
        changeDefaultKeyboardJson(-1, Locale.ENGLISH, "telugu")
        florisboard.setActiveInput(R.id.media_input)
    }

    //This Method is use to Set Default  Keyboard Language

    fun setDefaultKeyboard(mode: Int) {
        changeDefaultKeyboardJson(
            -1,
            Locale.ENGLISH,
            "tamil"
        )  // setting the default for handling keyboard size large issue
        if (mode == 0) {
            setActiveKeyboardMode(KeyboardMode.SYMBOLS)
        } else {
            setActiveKeyboardMode(KeyboardMode.SYMBOLS2)
        }
    }


    override fun onViewClick(focusChanged: Boolean) {
        textTranslationSwitchOff()
    }
}
