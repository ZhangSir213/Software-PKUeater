package com.example.psycho.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.psycho.R

/**
 * created by ZhangSir213 @ 2022/10/26
 */
class BorderTextView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs) {

    private var strokeWidth: Float // 边框线宽
    private var strokeColor: Int // 边框颜色
    private var cornerRadius: Float // 圆角半径
    private var isTextSameColor: Boolean // 边框字体颜色相同

    private var borderSkipLeft: Boolean
    private var borderSkipTop: Boolean
    private var borderSkipRight: Boolean
    private var borderSkipBottom: Boolean

    private val paint: Paint
    private val rectF: RectF

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BorderTextView)
        strokeColor = ta.getColor(R.styleable.BorderTextView_BorderStrokeColor, Color.GRAY)
        strokeWidth = ta.getDimension(R.styleable.BorderTextView_BorderStrokeWidth, 0f)
        cornerRadius = ta.getDimension(R.styleable.BorderTextView_BorderCornerRadius, 0f)
        isTextSameColor = ta.getBoolean(R.styleable.BorderTextView_BorderTextSameColor, false)
        borderSkipLeft = ta.getBoolean(R.styleable.BorderTextView_BorderSkipLeft, false)
        borderSkipTop = ta.getBoolean(R.styleable.BorderTextView_BorderSkipTop, false)
        borderSkipRight = ta.getBoolean(R.styleable.BorderTextView_BorderSkipRight, false)
        borderSkipBottom = ta.getBoolean(R.styleable.BorderTextView_BorderSkipBottom, false)
        ta.recycle()

        paint = Paint().apply {
            style = Paint.Style.STROKE // 画笔样式
            color = strokeColor // 颜色
            isAntiAlias = true // 无锯齿
            strokeWidth = this@BorderTextView.strokeWidth // 宽度
        }
        rectF = RectF()
        if (isTextSameColor) setTextColor(strokeColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val viewWidth = measuredWidth.toFloat()
        val viewHeight = measuredHeight.toFloat()
        canvas?.apply {
            if (cornerRadius <= 0) { // 当有设置圆角时，使用drawRoundRect来画边框
                if (!borderSkipLeft) drawLine(0f, viewHeight, 0f, 0f, paint)
                if (!borderSkipTop) drawLine(0f, 0f, viewWidth, 0f, paint)
                if (!borderSkipRight) drawLine(viewWidth, 0f, viewWidth, viewHeight, paint)
                if (!borderSkipBottom) drawLine(viewWidth, viewHeight, 0f, viewHeight, paint)
            } else {
                rectF.left = 0f
                rectF.top = rectF.left
                rectF.right = viewWidth
                rectF.bottom = viewHeight
                drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
            }
        }
    }

    private fun dip2px(defaultValue: Float): Float {
        return defaultValue * context.resources.displayMetrics.density + 0.5f
    }

    private fun px2dip(defaultValue: Float): Float {
        return defaultValue / defaultValue * context.resources.displayMetrics.density + 0.5f
    }

}
