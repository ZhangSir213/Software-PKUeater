package com.example.psycho.ui.log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.ActivityLogBinding

class LogActivity : AppCompatActivity() {
    lateinit var binding : ActivityLogBinding
    private val userData = Data
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        val button_breakfast:ImageButton = binding.breakfast
        val button_lunch:ImageButton = binding.lunch
        val button_dinner:ImageButton = binding.dinner
        val button_snack:ImageButton = binding.snack

        val view = binding.root
        //userData.initDietLog()
        setContentView(view)

        button_breakfast.setOnClickListener {
            //TODO: 设置传入参数，标记新增的是早餐
            val intent:Intent = Intent(this, SelectFoodActivity::class.java)
            intent.putExtra("meal",1)
            startActivity(intent)
        }

        button_lunch.setOnClickListener {
            val intent:Intent = Intent(this, SelectFoodActivity::class.java)
            intent.putExtra("meal",2)
            startActivity(intent)
        }

        button_dinner.setOnClickListener {
            val intent:Intent = Intent(this, SelectFoodActivity::class.java)
            intent.putExtra("meal",3)
            startActivity(intent)
        }
        button_snack.setOnClickListener {
            val intent:Intent = Intent(this, SelectFoodActivity::class.java)
            intent.putExtra("meal",4)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if(userData.checkDietLog()){//若存在已有的日志，则不显示提示图标和语句
            binding.emptyIcon.visibility = View.INVISIBLE
            binding.emptyText.visibility = View.INVISIBLE
        }else{
            binding.emptyIcon.visibility = View.VISIBLE
            binding.emptyText.visibility = View.VISIBLE
            showDietLog()
        }

    }

    /*
    **已有日志，返回true，否则返回false
     */

    private fun showDietLog(){//获取并展示日志内容
        //获取日志内容
        val breakfastLog : List<String> = userData.getDietLog(1)
        val lunchLog : List<String> = userData.getDietLog(2)
        val dinnerLog : List<String> = userData.getDietLog(3)
        val snackLog : List<String> = userData.getDietLog(4)
        //TODO:展示日志内容
        Toast.makeText(this,breakfastLog.toString(),Toast.LENGTH_SHORT).show()
    }
}