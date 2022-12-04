package com.example.psycho.ui.custom

import android.content.Context

object SizeUtil {
    //px转换为dp
    fun px2dp(context: Context, pxValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (pxValue / density + 0.5f).toInt()
    }

    //dp转换为px
    fun dp2px(context: Context, dpValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

    //px转换为sp
    fun px2sp(context: Context, pxValue: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (pxValue / scaledDensity + 0.5f).toInt()
    }

    //sp转换为px
    fun sp2px(context: Context, spValue: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (spValue * scaledDensity + 0.5f).toInt()
    }
}
