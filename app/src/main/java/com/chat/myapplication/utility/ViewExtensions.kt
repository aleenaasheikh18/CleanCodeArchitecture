package com.chat.myapplication.utility

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
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

