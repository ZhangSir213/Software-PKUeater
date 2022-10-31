package com.example.psycho.resource
import android.content.Context
import com.example.psycho.R

class Canteen(val context:Context) {
    fun getCanteenList():Array<String> {
        return context.resources.getStringArray(R.array.canteen_array)
    }
}