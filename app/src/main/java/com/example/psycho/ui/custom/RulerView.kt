package com.example.psycho.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import com.example.psycho.R

/**
 * Created by juan on 2018/5/11.
 */
class RulerView: View {
    private val TAG : String = "RulerView"

    private var mMinVelocity:Int = 0
    private var mScroller: Scroller? = null//Scroller是一个专门用于处理滚动效果的工具类   用mScroller记录/计算View滚动的位置，再重写View的computeScroll()，完成实际的滚动
    private var mVelocityTracker: VelocityTracker?=null//主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率。
    private var mWidth:Int = 0
    private var mHeight:Int = 0

    private var mSelectorValue=50f      // 未选择时 默认的值 滑动后表示当前中间指针正在指着的值
    private var mMaxValue=200f          // 最大数值
    private var mMinValue=100f          //最小的数值
    private var mPerValue=1f            //最小单位(如 1:表示每2条刻度差为1;0.1:表示每2条刻度差为0.1

    private var mLineSpaceWidth = 5f    //  尺子刻度2条线之间的距离
    private var mLineWidth = 4f         //  尺子刻度的宽度
    private var mLineMaxHeight = 420f   //  尺子刻度分为3中不同的高度。 mLineMaxHeight表示最长的那根(也就是 10的倍数时的高度)
    private var mLineMidHeight = 30f    //  mLineMidHeight  表示中间的高度(也就是 5  15 25 等时的高度)
    private var mLineMinHeight = 17f    //  mLineMinHeight  表示最短的那个高度(也就是 1 2 3 4 等时的高度)

    private var mTextMarginTop = 10f
    private var mTextSize = 30f          //尺子刻度下方数字的大小
    private var mAlphaEnable=false       // 尺子 最左边 最后边是否需要透明 `(透明效果更好点)
    private var mTextHeight = 0.toFloat()//尺子刻度下方数字的高度
    private var mTextPaint: Paint?=null   // 尺子刻度下方数字(也就是每隔10个出现的数值)画笔
    private var mLinePaint: Paint?=null   //  尺子刻度线的画笔

    private var mTotalLine:Int = 0       //共有多少条 刻度
    private var mMaxOffset:Int = 0       //所有刻度 共有多长
    private var mOffset:Float = 0.toFloat()// 默认状态下，mSelectorValue所在的位置  位于尺子总刻度的位置
    private var mLastX:Int = 0
    private var mMove: Int = 0
    private lateinit var mListener: OnValueChangeListener// 滑动后数值回调

    private var mLineColor:Int= Color.GRAY //刻度的颜色
    private var mTextColor:Int= Color.BLACK//文字的颜色

    constructor(mContext: Context) : this(mContext,null)

    constructor(mContext: Context, attrs: AttributeSet?) : this(mContext, attrs!!,0)

    constructor(mContext: Context, attrs: AttributeSet,defStyleAttr:Int) : super(mContext, attrs,defStyleAttr) {
        init(mContext, attrs)
    }

    fun init(context: Context, attrs: AttributeSet){
        Log.d(TAG, "init")
        mScroller= Scroller(context)

        this.mLineSpaceWidth=myfloat(25.0f)
        this.mLineWidth=myfloat(2.0f)
        this.mLineMaxHeight=myfloat(100.0f)
        this.mLineMidHeight=myfloat(60.0f)
        this.mLineMinHeight=myfloat(40.0f)
        this.mTextHeight=myfloat(40.0f)

        val typedArray: TypedArray =context.obtainStyledAttributes(attrs,
            R.styleable.RulerView)

        mAlphaEnable= typedArray.getBoolean(R.styleable.RulerView_alphaEnable, mAlphaEnable)

        mLineSpaceWidth = typedArray.getDimension(R.styleable.RulerView_lineSpaceWidth, mLineSpaceWidth)
        mLineWidth = typedArray.getDimension(R.styleable.RulerView_lineWidth, mLineWidth)
        mLineMaxHeight = typedArray.getDimension(R.styleable.RulerView_lineMaxHeight, mLineMaxHeight)
        mLineMidHeight = typedArray.getDimension(R.styleable.RulerView_lineMidHeight, mLineMidHeight)
        mLineMinHeight = typedArray.getDimension(R.styleable.RulerView_lineMinHeight, mLineMinHeight)
        mLineColor = typedArray.getColor(R.styleable.RulerView_lineColor, mLineColor)

        mTextSize = typedArray.getDimension(R.styleable.RulerView_textSize, mTextSize)
        mTextColor = typedArray.getColor(R.styleable.RulerView_textColor, mTextColor)
        mTextMarginTop = typedArray.getDimension(R.styleable.RulerView_textMarginTop, mTextMarginTop)

        mSelectorValue = typedArray.getFloat(R.styleable.RulerView_selectorValue, 0.0f)
        mMinValue = typedArray.getFloat(R.styleable.RulerView_minValue, 0.0f)
        mMaxValue = typedArray.getFloat(R.styleable.RulerView_maxValue, 100.0f)
        mPerValue = typedArray.getFloat(R.styleable.RulerView_perValue, 0.1f)

        mMinVelocity= ViewConfiguration.get(getContext()).scaledMinimumFlingVelocity

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.textSize = mTextSize
        mTextPaint!!.color = mTextColor
        mTextHeight = getFontHeight(mTextPaint!!)

        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint!!.strokeWidth = mLineWidth
        mLinePaint!!.color = mLineColor
    }

    private fun myfloat(paramFloat:Float):Float{
        return 0.5f+paramFloat*1.0f
    }

    private fun getFontHeight(paint: Paint):Float{
        val fm = paint.fontMetrics
        return fm.descent - fm.ascent
    }

    /**
     * 设置默认的参数
     * @param selectorValue 未选择时 默认的值 滑动后表示当前中间指针正在指着的值
     * @param minValue   最大数值
     * @param maxValue   最小的数值
     * @param per   最小单位(如1:表示每2条刻度差为1;0.1:表示每2条刻度差为0.1;其中身高mPerValue为1,体重mPerValue 为0.1)
     */
    fun setValue(selectorValue: Float, minValue: Float, maxValue: Float, per: Float) {
        this.mSelectorValue = selectorValue
        this.mMaxValue = maxValue
        this.mMinValue = minValue
        this.mPerValue = per * 10.0f
        this.mTotalLine = ((mMaxValue * 10 - mMinValue * 10) / mPerValue).toInt() + 1


        mMaxOffset = (-(mTotalLine - 1) * mLineSpaceWidth).toInt()
        mOffset = (mMinValue - mSelectorValue) / mPerValue * mLineSpaceWidth * 10f
        Log.d(TAG, "mOffset:" + mOffset + ",mMaxOffset:" + mMaxOffset
                + ",mTotalLine:" + mTotalLine)
        invalidate()
        visibility = View.VISIBLE
    }

    fun setOnValueChangeListener(listener: OnValueChangeListener) {
        mListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            mWidth = w
            mHeight = h
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var left: Float
        var height: Float
        var value: String
        var alpha = 0
        var scale: Float
        val srcPointX = mWidth / 2
        for (i in 0 until mTotalLine) {
            left = srcPointX.toFloat() + mOffset + i * mLineSpaceWidth

            if (left < 0 || left > mWidth) {
                continue  //先画默认值在正中间，左右各一半的view。多余部分暂时不画(也就是从默认值在中间，画旁边左右的刻度线)
            }

            if (i % 10 == 0) {
                height = mLineMaxHeight
            } else if (i % 5 == 0) {
                height = mLineMidHeight
            } else {
                height = mLineMinHeight
            }
            if (mAlphaEnable) {
                scale = 1 - Math.abs(left - srcPointX) / srcPointX
                alpha = (255f * scale * scale).toInt()

                mLinePaint!!.setAlpha(alpha)
            }
            canvas.drawLine(left, 0f, left, height, mLinePaint!!)

            if (i % 10 == 0) {
                value = (mMinValue + i * mPerValue / 10).toInt().toString()
                if (mAlphaEnable) {
                    mTextPaint!!.alpha = alpha
                }
                canvas.drawText(value, left - mTextPaint!!.measureText(value) / 2,
                    height + mTextMarginTop + mTextHeight, mTextPaint!!)    // 在为整数时,画 数值
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent")

        val action = event.action
        val xPosition = event.x.toInt()

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mScroller!!.forceFinished(true)
                mLastX = xPosition
                mMove = 0
            }
            MotionEvent.ACTION_MOVE -> {
                mMove = mLastX - xPosition
                changeMoveAndValue()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                countMoveEnd()
                countVelocityTracker()
                return false
            }
            else -> {
            }
        }

        mLastX = xPosition
        return true
    }

    private fun countVelocityTracker() {
        Log.d(TAG, "countVelocityTracker")
        mVelocityTracker!!.computeCurrentVelocity(1000)  //初始化速率的单位
        val xVelocity = mVelocityTracker!!.xVelocity //当前的速度
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller!!.fling(0, 0, xVelocity.toInt(), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0)
        }
    }

    /**
     * 滑动结束后，若是指针在2条刻度之间时，改变mOffset 让指针正好在刻度上。
     */
    private fun countMoveEnd() {
        mOffset -= mMove.toFloat()
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
        } else if (mOffset >= 0) {
            mOffset = 0f
        }

        mLastX = 0
        mMove = 0

        mSelectorValue = mMinValue + Math.round(Math.abs(mOffset) * 1.0f / mLineSpaceWidth) * mPerValue / 10.0f
        mOffset = (mMinValue - mSelectorValue) * 10.0f / mPerValue * mLineSpaceWidth

        notifyValueChange()
        postInvalidate()
    }

    /**
     * 滑动后的操作
     */
    private fun changeMoveAndValue() {
        mOffset -= mMove.toFloat()

        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
            mMove = 0
            mScroller!!.forceFinished(true)
        } else if (mOffset >= 0) {
            mMove = 0
            mScroller!!.forceFinished(true)
        }
        mSelectorValue = mMinValue + Math.round(Math.abs(mOffset) * 1.0f / mLineSpaceWidth) * mPerValue / 10.0f


        notifyValueChange()
        postInvalidate()
    }

    private fun notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mSelectorValue)
        }
    }

    /**
     * 滑动后的回调
     */
    interface OnValueChangeListener{
        fun onValueChange(value: Float)
    }

    override fun computeScroll() {
        Log.d(TAG, "computeScroll")
        super.computeScroll()
        if (mScroller!!.computeScrollOffset()) {//mScroller.computeScrollOffset()返回true表示滑动还没有结束
            if (mScroller!!.currX == mScroller!!.finalX) {
                countMoveEnd()
            } else {
                val xPosition = mScroller!!.currX
                mMove = mLastX - xPosition
                changeMoveAndValue()
                mLastX = xPosition
            }
        }
    }
}