package com.chat.myapplication.utility

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.createBitmap

/**
 *  Extension functions for view visibility
 * **/
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.takeScreenShot(): Bitmap {
    val bitmap = createBitmap(this.width, this.height)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

fun View.setOnSingleClickListener(l: (View) -> Unit) = setOnClickListener(OnSingleClickListener(l))

/**
 * Extension function to handle click listener on view
 */
fun View.setOnSingleClickListener(l: View.OnClickListener) =
    setOnClickListener(OnSingleClickListener(l))

/**
 * Data class to hold clickable text configuration
 */
data class ClickableText(
    val text: String,
    @ColorInt val color: Int,
    val underline: Boolean = true,
    val onClick: () -> Unit
)

/**
 * Extension function to set spannable text with multiple clickable parts
 * @param fullText The complete text to display
 * @param clickableTexts List of ClickableText configurations
 */
fun TextView.setClickableText(
    fullText: String,
    clickableTexts: List<ClickableText>
) {
    val spannableString = SpannableString(fullText)

    clickableTexts.forEach { clickableText ->
        val startIndex = fullText.indexOf(clickableText.text)
        if (startIndex >= 0) {
            val endIndex = startIndex + clickableText.text.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    clickableText.onClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = clickableText.color
                    ds.isUnderlineText = clickableText.underline
                }
            }

            spannableString.setSpan(
                clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = android.graphics.Color.TRANSPARENT
}
