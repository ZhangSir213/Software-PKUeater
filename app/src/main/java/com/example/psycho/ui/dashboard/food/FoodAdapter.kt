package com.example.psycho.ui.log

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.psycho.R
import com.squareup.picasso.Picasso


class Food(val name:String, val imageUrl: String)

class FoodAdapter(activity: Activity, val resourceId: Int, data: List<Food>):
        ArrayAdapter<Food>(activity, resourceId, data){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        }else {
            view = convertView
        }
        val foodImage: ImageView = view.findViewById(R.id.foodImage)
        val foodName: TextView = view.findViewById(R.id.foodName)
        val food = getItem(position)
        if (food != null){
            //foodImage.setImageResource(food.imageId)
            Picasso.with(context).load("http://47.94.139.212:3000"+food.imageUrl).into(foodImage)
            foodName.text = food.name
        }
        return view
    }
        }