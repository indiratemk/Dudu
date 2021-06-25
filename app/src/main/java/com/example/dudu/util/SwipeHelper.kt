package com.example.dudu.util

import android.animation.ObjectAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeHelper :
    ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    private lateinit var leftButton: ControlButton
    private lateinit var rightButton: ControlButton

    abstract fun createLeftButton(): ControlButton

    abstract fun createRightButton(): ControlButton

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> {
                rightButton.onSwipeEvent(position)
            }
            ItemTouchHelper.RIGHT -> {
//                ObjectAnimator.ofFloat(viewHolder.itemView,
//                    "translationX",
//                    0f).apply {
//                    duration = 2000
//                    start()
//                }
                leftButton.onSwipeEvent(position)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                rightButton = createRightButton()
                onDrawRightButton(itemView, c, dX)
            } else if (dX > 0) {
                leftButton = createLeftButton()
                onDrawLeftButton(itemView, c, dX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun onDrawRightButton(
        itemView: View,
        canvas: Canvas,
        translationX: Float
    ) {
        val right = itemView.right.toFloat()
        val left = right + translationX
        rightButton.onDraw(canvas, RectF(left, itemView.top.toFloat(),
            right, itemView.bottom.toFloat()), left + itemView.height / 2)
    }

    private fun onDrawLeftButton(
        itemView: View,
        canvas: Canvas,
        translationX: Float
    ) {
        val left = itemView.left.toFloat()
        val right = left + translationX
        leftButton.onDraw(canvas, RectF(left, itemView.top.toFloat(),
            right, itemView.bottom.toFloat()), right - itemView.height / 2)
    }


    class ControlButton(
        private val color: Int,
        private val drawable: Drawable?,
        val onSwipeEvent: (Int) -> Unit
    ) {

        fun onDraw(canvas: Canvas, rectF: RectF, startPosition: Float) {
            val paint = Paint()
            paint.color = color
            canvas.drawRect(rectF, paint)

            drawable?.let {
                val bitmap = drawableToBitmap(it)
                canvas.drawBitmap(bitmap, startPosition - bitmap.width / 2,
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