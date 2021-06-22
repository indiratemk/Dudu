package com.example.dudu

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeHelper :
    ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    abstract fun createLeftButtons(): List<ControlButton>

    abstract fun createRightButtons(): List<ControlButton>

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var translationX = dX
        val itemView = viewHolder.itemView
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                val rightButtons = createRightButtons()
                translationX = dX * rightButtons.size * itemView.height / itemView.width
                onDrawRightButton(itemView, c, translationX, rightButtons)
            } else if (dX > 0) {
                val leftButtons = createLeftButtons()
                translationX = dX * leftButtons.size * itemView.height / itemView.width
                onDrawLeftButton(itemView, c, translationX, leftButtons)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
    }

    private fun onDrawRightButton(
        itemView: View,
        canvas: Canvas,
        translationX: Float,
        buttons: List<ControlButton>
    ) {
        var right = itemView.right.toFloat()
        val btnWidth = -1 * translationX / buttons.size
        for (button in buttons) {
            val left = right - btnWidth
            button.onDraw(canvas, RectF(left, itemView.top.toFloat(),
                right, itemView.bottom.toFloat()))
            right = left
        }
    }

    private fun onDrawLeftButton(
        itemView: View,
        canvas: Canvas,
        translationX: Float,
        buttons: List<ControlButton>
    ) {
        var left = itemView.left.toFloat()
        val btnWidth = translationX / buttons.size
        for (button in buttons) {
            val right = left + btnWidth
            button.onDraw(canvas, RectF(left, itemView.top.toFloat(),
                right, itemView.bottom.toFloat()))
            left = right
        }
    }


    class ControlButton(
        private val color: Int,
        private val drawable: Drawable?
    ) {

        fun onDraw(canvas: Canvas, rectF: RectF) {
            val paint = Paint()
            paint.color = color
            canvas.drawRect(rectF, paint)

            drawable?.let {
                val bitmap = drawableToBitmap(it)
                canvas.drawBitmap(bitmap, rectF.centerX() - bitmap.width / 2,
                    rectF.centerY() - bitmap.height / 2, null)
            }
        }

        private fun drawableToBitmap(drawable: Drawable): Bitmap {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}