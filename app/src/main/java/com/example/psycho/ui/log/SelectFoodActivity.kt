package com.example.psycho.ui.log

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.ActivitySelectfoodBinding

class SelectFoodActivity : AppCompatActivity() {
    private val foodList = ArrayList<Food>()
    private val userData = Data
    lateinit var binding: ActivitySelectfoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectfoodBinding.inflate(layoutInflater)
        val meal = intent.getIntExtra("meal",0)
        val listView = binding.listView
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFoods()
        val adapter = FoodAdapter(this, R.layout.food_item, foodList)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val food = foodList[position]
            userData.addDietLog(meal, food.name)//添加日志记录
            finish()
        }



    }
    private fun initFoods(){
        //TODO:获取真实菜品列表
        foodList.add(Food("辣子鸡",R.drawable.chicken))
        foodList.add(Food("红豆粥",R.drawable.redbean))
        foodList.add(Food("奥尔良鸡腿",R.drawable.chickenleg))
        foodList.add(Food("土豆丝",R.drawable.potato))
        foodList.add(Food("油麦菜",R.drawable.vagetable))
        foodList.add(Food("煎饼果子",R.drawable.pancake))
        foodList.add(Food("枣糕",R.drawable.cake))
        foodList.add(Food("米饭",R.drawable.rice))


    }
}