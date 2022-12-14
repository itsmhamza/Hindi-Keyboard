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

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import dev.patrickgold.florisboard.ime.core.PrefHelper

/**
 * Basically the same as an ImageButton.
 * @see [onMeasure] why this view class exists.
 */
class SmartbarQuickActionButton : androidx.appcompat.widget.AppCompatImageButton {
    private val prefs: PrefHelper = PrefHelper.getDefaultInstance(context)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        updateTheme()
    }

    /**
     * Override onMeasure() to automatically set the height of the button equal to the width of
     * the button. The height is MATCH_PARENT and the exact same calculated pixel size should be
     * used for the width.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        updateTheme()
        super.onDraw(canvas)
    }

    private fun updateTheme() {
//        setBackgroundTintColor2(this, prefs.theme.smartbarButtonBgColor)
//        setColorFilter(prefs.theme.smartbarButtonFgColor)
    }
}
