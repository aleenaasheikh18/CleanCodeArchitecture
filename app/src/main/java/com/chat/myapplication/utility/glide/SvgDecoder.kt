package com.chat.myapplication.utility.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.caverock.androidsvg.SVG
import java.io.InputStream

class SvgDecoder : ResourceDecoder<InputStream, SVG> {

    override fun handles(source: InputStream, options: Options): Boolean {
        return true
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<SVG>? {
        return try {
            val svg = SVG.getFromInputStream(source)
            if (width != com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) {
                svg.documentWidth = width.toFloat()
            }
            if (height != com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) {
                svg.documentHeight = height.toFloat()
            }
            SimpleResource(svg)
        } catch (e: Exception) {
            null
        }
    }
}
