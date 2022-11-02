package com.example.psycho.ui.setting

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.FragmentSettingBinding
import com.example.psycho.ui.login.LoginActivity
import com.example.psycho.ui.setting.usage.CountActivity
import java.util.*


class SettingFragment : Fragment() {
    private var _data = Data
    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /*
        var mWeightRuler: RulerView? = null
        var mHeightRuler: RulerView? = null
        var mTvWeight: TextView? = null
        var mTvHeight: TextView? = null
        var weighted: Float = 55f
        var height: Int = 165
     */
    /**
     * 重启应用
     * @param context
     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)
        /*
        val view : View = inflater.inflate(R.layout.fragment_setting,null)
        mWeightRuler = view.findViewById(R.id.ruler_weight)
        mHeightRuler = view.findViewById(R.id.ruler_height)
        mTvWeight= view.findViewById(R.id.tv_weight)
        mTvHeight= view.findViewById(R.id.tv_height)

         */

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textSetting
        settingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val imageView:ImageView = binding.imageTopBg
        val buttonCount:ImageButton = binding.buttonSetting
        val textWeight: TextView = binding.textWeight
        val textHeight: TextView = binding.textHeight
        val buttonLogout:Button=binding.logout
        val imageViewGener:ImageView=binding.imageViewGender
        val timer = Timer()
        val buttonVisibleW:ImageButton = binding.buttonEyeWeight
        val textVisToolW:TextView = binding.textViewToolWeightVis
        val buttonVisibleH:ImageButton = binding.buttonEyeHeight
        val textVisToolH:TextView = binding.textViewToolHeightVis

        timer.schedule(object : TimerTask() {
            override fun run() {
                //在这里更新数据
                activity?.runOnUiThread {
                    if(_data.getTimerFlag()==true) {
                        if (_data.getModifyFlag()) {
                            if (_data.getWeightVisible())
                            {
                                textWeight.text =
                                    _data.getTrueWeight().toFloat().toString().format("%.1f")
                                textVisToolW.text="visible"
                                buttonVisibleW.setImageDrawable(resources.getDrawable(R.drawable.icon_visible_on))
                            }
                            else
                            {
                                textVisToolW.text="invisible"
                                buttonVisibleW.setImageDrawable(resources.getDrawable(R.drawable.icon_visible_off))
                                textWeight.text="***"
                            }
                            if(_data.getHeightVisible())
                            {
                                textVisToolH.text="visible"
                                buttonVisibleH.setImageDrawable(resources.getDrawable(R.drawable.icon_visible_on))
                                textHeight.text=_data.getTrueHeight().toString()
                            }
                            else
                            {
                                textVisToolH.text="invisible"
                                buttonVisibleH.setImageDrawable(resources.getDrawable(R.drawable.icon_visible_off))
                                textHeight.text="***"
                            }
                            Log.d("Flush","Flush")
                            if (_data.getGender()==1) {
                                imageViewGener.setImageResource(R.drawable.boy)
                            }
                            else{
                                imageViewGener.setImageResource(R.drawable.girl)
                            }
                        }
                    }
                }
            }
        }, 0, 500) //延迟10毫秒后，执行一次task


        buttonLogout.setOnClickListener {

            _data.deleteUser()
            val act : FragmentActivity? =getActivity()
            val intentL:Intent = Intent(act,LoginActivity::class.java)
            startActivity(intentL)
            activity?.finish()
            //val manager=activity?.getSystemService(Context.ACTIVITY_SERVICE)
            //manager as ActivityManager
            //manager.restartPackage("com.example.psycho")

        }

        buttonCount.setOnClickListener(View.OnClickListener() {
            val act : FragmentActivity? =getActivity()
            val intent:Intent = Intent(act,CountActivity::class.java)
            act?.startActivityForResult(intent, 1)
            println("跳转到身高体重调整界面")
        })

        buttonVisibleW.setOnClickListener(View.OnClickListener() {
            if(textVisToolW.text=="visible") {
                _data.setWeightInvisible()
            }
            else if(textVisToolW.text=="invisible") {
                _data.setWeightVisible()
            }
        })

        buttonVisibleH.setOnClickListener(View.OnClickListener() {
            if(textVisToolH.text=="visible") {
                _data.setHeightInvisible()
            }
            else if(textVisToolH.text=="invisible") {
                _data.setHeightVisible()
            }
        })
        return root
    }
    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activityCount:ImageButton = requireActivity().findViewById(R.id.button_setting)
        activityCount.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(),CountActivity::class.java)
        })
    }
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}