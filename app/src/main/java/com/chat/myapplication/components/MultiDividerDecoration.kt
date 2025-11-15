import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

class MultiDividerDecoration(
    private val drawable: Drawable,
    private val positions: List<Int>,
    private val spaceAbove: Int = 0,
    private val spaceBelow: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            if (!positions.contains(i)) continue

            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            // Draw line in the middle of the available space
            val top = child.bottom + params.bottomMargin + spaceAbove
            val bottom = top + drawable.intrinsicHeight
            val left = child.left
            val right = child.right

            drawable.setBounds(left, top, right, bottom)
            drawable.draw(c)
        }
    }

    // This adds spacing **without stretching the drawable**
    override fun getItemOffsets(
        outRect: android.graphics.Rect,
        view: android.view.View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        if (positions.contains(pos)) {
            outRect.top = spaceAbove
            outRect.bottom = spaceBelow
        }
    }
}
