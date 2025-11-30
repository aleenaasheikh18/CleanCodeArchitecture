package com.chat.myapplication.utility.glide

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder

fun ImageView.loadSvg(url: String?) {
    if (url.isNullOrEmpty()) return

    this.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)

    Glide.with(this.context)
        .`as`(PictureDrawable::class.java)
        .load(url)
        .into(this)
}

fun RequestBuilder<PictureDrawable>.loadSvgInto(imageView: ImageView) {
    imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
    this.into(imageView)
}
