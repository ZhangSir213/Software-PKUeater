package com.example.psycho.ui.setting.usage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.example.psycho.MainActivity
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.ui.custom.RulerView
import com.example.psycho.ui.login.LoginActivity
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.DateTimePicker
import java.util.*

class CountActivity : AppCompatActivity() {
    private var mWeightRuler: RulerView? = null
    private var mHeightRuler: RulerView? = null
    private var mTvWeight: TextView? = null
    private var mTvHeight: TextView? = null
    private var _data = Data
    private var weight: Double = _data.getTrueWeight()
    private var height: Int = _data.getTrueHeight()

    private var gender:Int=1
    private var monthOfYear:Int=0
    private var dayOfMonth:Int=0
    private var year:Int=0
    private var date:Date=Date(0)

    private fun dateLayout()
    {
        setContentView(R.layout.datapicker)
        val picker:DateTimePicker=findViewById(R.id.picker)
        picker.setDisplayType(intArrayOf(
            DateTimeConfig.YEAR,//显示年
            DateTimeConfig.MONTH,//显示月
            DateTimeConfig.DAY))

        picker.showLabel(true)
        picker.setOnDateTimeChangedListener {
            var calendar  = Calendar.getInstance()
            calendar.timeInMillis = it
            val time=calendar.time
            date=time
            monthOfYear=time.month+1
            year=time.year+1900
            dayOfMonth=time.date
            Log.d("Date",date.toString())
            Log.d("Month",monthOfYear.toString())
            Log.d("Year",year.toString())
            Log.d("Day",dayOfMonth.toString())

        }
        val btn_back:Button=findViewById(R.id.btn_back)
        btn_back.setOnClickListener{
            selectorLayout()
        }
        val btn_confirm:Button=findViewById(R.id.btn_confirm)
        btn_confirm.setOnClickListener{
            _data.setDay(dayOfMonth)
            _data.setMonth(monthOfYear)
            _data.setYear(year)
            Log.d("Finish","Count")
            _data.setFirstFlag()

            finish()
        }

    }

    private fun selectorLayout()
    {
        setContentView(R.layout.sex_selector)
        Log.d("Weight",weight.toString())
        Log.d("Height",height.toString())
        val button_back:Button=findViewById(R.id.btn_back)
        button_back.setOnClickListener {
            mainLayout()
        }
        val button_next:Button=findViewById(R.id.btn_date)
        button_next.setOnClickListener {
            dateLayout()
        }
        val check_sex: CheckBox =findViewById(R.id.btn_register_info_sex)
        check_sex.setOnClickListener{
            if (check_sex.isChecked())
                gender=2
            else
                gender=1
            Log.d("Gender",gender.toString())
            _data.setGender(gender)

        }
    }

    private fun mainLayout()
    {
        setContentView(R.layout.activity_count)
        mWeightRuler = findViewById(R.id.ruler_weight)
        mHeightRuler = findViewById(R.id.ruler_height)
        mTvWeight= findViewById(R.id.tv_weight)
        mTvHeight= findViewById(R.id.tv_height)
        val buttonQuit: Button = findViewById(R.id.button_count_quit)
        val buttonConfirm: Button = findViewById(R.id.button_confirm)

        buttonQuit.setOnClickListener {
            val intent=Intent()
            intent.putExtra("wei_hei", "$weight,$height")
            setResult(RESULT_OK, intent)
            finish()
        }
        buttonConfirm.setOnClickListener {
            _data.setTrueWeight(weight)
            _data.setTrueHeight(height)
            selectorLayout()
        }
        //体重的view
        mWeightRuler!!.setOnValueChangeListener(object : RulerView.OnValueChangeListener {
            override fun onValueChange(value: Float) {
                weight = value.toDouble()
                mTvWeight!!.text = weight.toFloat().toString() + "kg"
            }
        })
        mWeightRuler!!.setValue(55f, 20f, 200f, 0.1f)
        mTvWeight!!.text = weight.toFloat().toString() + "kg"

        //身高的view
        mHeightRuler!!.setOnValueChangeListener(object : RulerView.OnValueChangeListener {
            override fun onValueChange(value: Float) {
                height = value.toInt()
                mTvHeight!!.text = height.toString() + "cm"
            }
        })
        mHeightRuler!!.setValue(165f, 80f, 250f, 1f)
        mTvHeight!!.text = height.toString() + "cm"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainLayout()

    }
}