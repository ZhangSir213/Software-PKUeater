package com.example.psycho.ui.setting.usage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.ui.custom.RulerView

class CountActivity : AppCompatActivity() {
    private var mWeightRuler: RulerView? = null
    private var mHeightRuler: RulerView? = null
    private var mTvWeight: TextView? = null
    private var mTvHeight: TextView? = null
    private var _data = Data
    private var weight: Double = _data.getTrueWeight()
    private var height: Int = _data.getTrueHeight()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            finish()
        }
        //体重的view
        mWeightRuler!!.setOnValueChangeListener(object : RulerView.OnValueChangeListener {
            override fun onValueChange(value: Float) {
                weight = value.toDouble()
                mTvWeight!!.text = weight.toString() + "kg"
            }
        })
        mWeightRuler!!.setValue(55f, 20f, 200f, 0.1f)
        mTvWeight!!.text = weight.toString() + "kg"

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
}