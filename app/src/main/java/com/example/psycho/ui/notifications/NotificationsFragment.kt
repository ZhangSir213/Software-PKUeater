package com.example.psycho.ui.notifications

import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.psycho.data.Data
import com.example.psycho.databinding.FragmentNotificationsBinding
import com.example.psycho.kernel.Kernel

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private var mListener: MySensorEventListener? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var mSensorManager: SensorManager? = null
    private val binding get() = _binding!!
    private var _data=Data

    inner class MySensorEventListener : SensorEventListener {
        var mStepDetector: Int = 0 // 自应用运行以来STEP_DETECTOR检测到的步数
        var mStepCounter: Int = 0  // 自系统开机以来STEP_COUNTER检测到的步数

        override fun onSensorChanged(event: SensorEvent) {
            println( event.sensor.type.toString() +"--" + Sensor.TYPE_STEP_DETECTOR + "--" + Sensor.TYPE_STEP_COUNTER)
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                if (event.values[0] == 1.0f) {
                    mStepDetector++
                }
            } else if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                mStepCounter = event.values[0].toInt()
            }
            _data.sensorSC(context!!)
            val desc = String.format("设备检测到您当前走了%d步，自开机以来总数为%d步", mStepDetector, mStepCounter)
            //Log.d("Sensor","==>$desc")
            //Toast.makeText(context!!,desc,Toast.LENGTH_SHORT).show()
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(mListener)
    }

    override fun onResume() {
        super.onResume()
        with(binding.circleProgressBarA) {  // 卡路里
            rindColorArray = intArrayOf(    // 渐变颜色
                Color.parseColor("#0888FF"),
                Color.parseColor("#6CD0FF")
            )
            max = Kernel.todayCalorie(context)
            val myCalorie = Data.getCalorieFromServer()
            descText = "卡路里$myCalorie/$max"
            startAnim(myCalorie)
        }

        with(binding.circleProgressBarB) {  // 步数
            rindColorArray = intArrayOf(
                Color.parseColor("#0888FF"),
                Color.parseColor("#6CD0FF")
            )
            max = 10000
            val myStep = Kernel.todayStep()
            descText = "步数$myStep/$max"
            startAnim(myStep)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val button2:Button=binding.btnsport
        mSensorManager = activity!!.getSystemService(SENSOR_SERVICE) as SensorManager
        mListener = MySensorEventListener()
        mSensorManager!!.registerListener(mListener, mSensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
            SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager!!.registerListener(mListener, mSensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
            SensorManager.SENSOR_DELAY_NORMAL)

        button2.setOnClickListener {
            _data.addSC()
            onResume()
        }
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text ="保持你的健康习惯，请随时查看哦~"
        }

        //val circleCpb1 = binding.circleProgressBarA
        //val circleCpb2 = binding.circleProgressBarB

        with(binding.circleProgressBarA) {  // 卡路里
            rindColorArray = intArrayOf(    // 渐变颜色
                Color.parseColor("#0888FF"),
                Color.parseColor("#6CD0FF")
            )
            max = Kernel.todayCalorie(context)
            val myCalorie = Data.getCalorieFromServer()
            descText = "卡路里$myCalorie/$max"
            startAnim(myCalorie)
        }

        with(binding.circleProgressBarB) {  // 步数
            rindColorArray = intArrayOf(
                Color.parseColor("#0888FF"),
                Color.parseColor("#6CD0FF")
            )
            max =10000
            val myStep = Kernel.todayStep()
            descText = "步数$myStep/$max"
            startAnim(myStep)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}