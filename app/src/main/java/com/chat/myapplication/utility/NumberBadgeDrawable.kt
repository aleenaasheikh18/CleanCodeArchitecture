package com.chat.myapplication.utility

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.chat.myapplication.R

class NumericBadgeDrawable(private val context: Context) : Drawable() {

    var count: Int = 0
        set(value) {
            field = value
            invalidateSelf()
        }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        style = Paint.Style.FILL
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 10f * context.resources.displayMetrics.density
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (count <= 0) return

        val displayText = if (count > 99) "99+" else count.toString()
        val padding = 2f * context.resources.displayMetrics.density

        val textWidth = paintText.measureText(displayText)
        val radius = (textWidth.coerceAtLeast(paintText.textSize) / 2) + padding

        val bounds = bounds

        // Minimal top-right position inside icon
        val cx = bounds.right - radius - padding
        val cy = bounds.top + radius + padding

        canvas.drawCircle(cx, cy, radius, paintCircle)

        val y = cy - (paintText.descent() + paintText.ascent()) / 2
        canvas.drawText(displayText, cx, y, paintText)
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
