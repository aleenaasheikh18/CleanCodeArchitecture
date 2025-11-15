package com.chat.myapplication.utility

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.chat.myapplication.R

class StringBadgeDrawable(private val context: Context) : Drawable() {

    var text: String = ""
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
        if (text.isEmpty()) return

        val padding = 1.5f * context.resources.displayMetrics.density

        val textWidth = paintText.measureText(text)
        val textHeight = paintText.fontMetrics.run { descent - ascent }

        val radius = ((textWidth.coerceAtLeast(textHeight)) / 2) + padding

        val bounds = bounds

        val cx = bounds.right.toFloat() - radius       // top-right corner horizontally
        val cy = bounds.top.toFloat() + radius        // top-right corner vertically

        canvas.drawCircle(cx, cy, radius, paintCircle)

        val y = cy - (paintText.descent() + paintText.ascent()) / 2
        canvas.drawText(text, cx, y, paintText)
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
