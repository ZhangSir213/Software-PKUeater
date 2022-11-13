package com.example.psycho.ui.log

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.ActivitySelectfoodBinding
import com.example.psycho.kernel.Kernel

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

        val foodNameList: List<String> = Kernel.getFoodlist()
        if(foodNameList.isEmpty()){
            return
        }
        for(i in foodNameList.indices)
            Kernel.getPictureId(foodNameList[i])?.let { Food(foodNameList[i], it) }
                ?.let { foodList.add(it) }
    }
}