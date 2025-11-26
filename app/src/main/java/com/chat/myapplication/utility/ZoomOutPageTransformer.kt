package com.chat.myapplication.utility

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val MIN_SCALE = 0.85f
        val MIN_ALPHA = 0.5f

        when {
            position < -1 -> {
                view.alpha = 0f
            }
            position <= 1 -> {
                val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                val vertMargin = view.height * (1 - scaleFactor) / 2
                val horzMargin = view.width * (1 - scaleFactor) / 2

                view.translationX = if (position < 0)
                    horzMargin - vertMargin / 2
                else
                    -horzMargin + vertMargin / 2

                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) /
                        (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> {
                view.alpha = 0f
            }
        }
    }
}
