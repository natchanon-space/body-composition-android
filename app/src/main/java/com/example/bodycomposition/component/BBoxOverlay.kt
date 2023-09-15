package com.example.bodycomposition.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.bodycomposition.R

class BBoxOverlay(context: Context?, attributesSet: AttributeSet?): View(context, attributesSet) {

    private val faceBoundingBox: MutableList<RectF> = mutableListOf()
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context!!, R.color.black)
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            faceBoundingBox.forEach {
                drawRect(it, paint)
                Log.d(TAG, "Draw bounding box: $it")
            }
        }
    }

    fun drawBox(bbox: List<RectF>) {
        faceBoundingBox.clear()
        faceBoundingBox.addAll(bbox)

        // Redraw on screen
        invalidate()
    }

    fun clearBox() {
        faceBoundingBox.clear()
        invalidate()
    }

    companion object {
        private const val TAG = "BBoxOverlay"
    }
}
