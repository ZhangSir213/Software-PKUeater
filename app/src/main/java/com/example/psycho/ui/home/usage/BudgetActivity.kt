package com.example.psycho.ui.home.usage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.psycho.data.Data
import com.example.psycho.databinding.ActivityLogin2Binding
import com.example.psycho.databinding.BudgetlayoutBinding
import com.example.psycho.databinding.FragmentHomeBinding
import com.loper7.date_time_picker.controller.DateTimeController
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import com.loper7.date_time_picker.number_picker.NumberPicker
import java.time.Year
import java.util.*

class BudgetActivity : AppCompatActivity() {
    private lateinit var binding: BudgetlayoutBinding
    private var hundred: NumberPicker? = null
    private var decade: NumberPicker? = null
    private var unit: NumberPicker? = null
    private var _data=Data
    var budget=0
    private fun syncDateData()
    {
        budget=0
        budget+=100*(hundred?.value)!!
        budget+=10*(decade?.value)!!
        budget+=1*(unit?.value)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BudgetlayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var selector = binding.includeBudget
        hundred= selector.npHundred
        decade=selector.npDecade
        unit=selector.npUnit
        budget=_data.getBudget(this)
        Log.d("Budget",budget.toString())
        hundred?.run {
            maxValue = 9
            minValue = 0
            value=(budget/100).toInt()
            setOnValueChangedListener { it, oldVal, newVal -> syncDateData() }
        }

        decade?.run {
            maxValue = 9
            minValue = 0
            value=((budget%100)/10).toInt()
            setOnValueChangedListener { it, oldVal, newVal -> syncDateData() }
        }
        unit?.run {
            maxValue = 9
            minValue = 0
            value=(budget%10).toInt()
            setOnValueChangedListener { it, oldVal, newVal -> syncDateData() }
        }
        val button1:Button=binding.btnConfirm
        button1.setOnClickListener {
            _data.setBudget(this@BudgetActivity,budget.toDouble())
            finish()
        }
        val button2:Button=binding.btnBack
        button2.setOnClickListener {
            finish()
        }
    }
}