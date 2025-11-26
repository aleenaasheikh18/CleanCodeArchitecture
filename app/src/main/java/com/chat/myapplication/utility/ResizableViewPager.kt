package com.chat.myapplication.utility

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.max

class WrapContentViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewPager = ViewPager2(context)

    init {
        addView(
            viewPager,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun getViewPager(): ViewPager2 = viewPager

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val recyclerView = viewPager.getChildAt(0) as? RecyclerView ?: return

        var maxHeight = 0

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )

            maxHeight = max(maxHeight, child.measuredHeight)
        }

        if (maxHeight > 0) {
            viewPager.layoutParams.height = maxHeight
            super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)
            )
        }
    }
}

