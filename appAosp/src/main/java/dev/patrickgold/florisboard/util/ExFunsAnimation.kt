package dev.patrickgold.florisboard.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import kotlin.math.hypot

fun View.showWithReveal() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // get the center for the clipping circle
        val cx = width / 2
        val cy = height / 2

        // get the final radius for the clipping circle
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
        // make the view visible and start the animation
        visibility = View.VISIBLE
        anim.start()

    } else {
        // set the view to invisible without a circular reveal animation below Lollipop
        this.visibility = View.INVISIBLE
    }
}

fun View.hideWithReveal() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // get the center for the clipping circle
        val cx = width / 2
        val cy = height / 2

        // get the initial radius for the clipping circle
        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.INVISIBLE
            }
        })

        // start the animation
        anim.start()
    } else {
        // set the view to visible without a circular reveal animation below Lollipop
        visibility = View.VISIBLE
    }
}