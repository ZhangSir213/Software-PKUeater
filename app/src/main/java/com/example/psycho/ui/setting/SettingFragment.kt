package com.example.psycho.ui.setting

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.FragmentSettingBinding
import com.example.psycho.kernel.Kernel
import com.example.psycho.resource.AcceptableAdapter
import com.example.psycho.resource.AvoidanceAdapter
import com.example.psycho.resource.CanteenAdapter
import com.example.psycho.ui.log.LogActivity
import com.example.psycho.ui.login.LoginActivity
import com.example.psycho.ui.setting.usage.CountActivity
import java.util.*


class SettingFragment : Fragment() {
    private var _data = Data
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    // Russian
    private var avoidanceList: List<String>? = null
    private var acceptableList: List<String>? = null
    private var recyclerViewAcceptable: RecyclerView? = null
    private var acceptableAdapter: AcceptableAdapter? = null
    private var recyclerViewAvoidance: RecyclerView? = null
    private var avoidanceAdapter: AvoidanceAdapter? = null

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
        val buttonLog:Button = binding.buttonLog
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
                    if(_data.getTimerFlag()) {
                        if (_data.getAvoidanceChange()) {
                            /*
                            avoidanceList = userData.getAvoidanceType()
                            recyclerViewAvoidance = binding.recyclerAvoidance
                            recyclerViewAvoidance?.layoutManager =
                                StaggeredGridLayoutManager(2,
                                    StaggeredGridLayoutManager.HORIZONTAL)
                            avoidanceAdapter = AvoidanceAdapter(dataList = avoidanceList)
                            recyclerViewAvoidance?.adapter = avoidanceAdapter


                            acceptableList = userData.getAcceptable()
                            recyclerViewAcceptable = binding.recyclerAcceptable
                            recyclerViewAcceptable?.layoutManager =
                                StaggeredGridLayoutManager(3,
                                    StaggeredGridLayoutManager.HORIZONTAL)
                            acceptableAdapter = AcceptableAdapter(dataList = acceptableList)
                            recyclerViewAcceptable?.adapter = acceptableAdapter
                            _data.setAvoidanceChange(false)
                             */
                        }

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

        // Roulette Avoidance
        // 把后面的List换成获取忌口清单的函数，要求返回值为List<String>
        avoidanceList = _data.getAvoidanceType()
        recyclerViewAvoidance = binding.recyclerAvoidance
        recyclerViewAvoidance?.layoutManager =
            StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.HORIZONTAL)
        avoidanceAdapter = AvoidanceAdapter(dataList = avoidanceList)
        recyclerViewAvoidance?.adapter = avoidanceAdapter
        avoidanceAdapter?.mOnRecyclerViewItemClick = object :
            AvoidanceAdapter.OnRecyclerViewItemClick<String> {
            override fun onItemClick(view: View?, t: String?, position: Int) {
                when (view?.id) {
                    R.id.content_avoidance -> {
                        t?.let {
                            _data.deleteAvoidance(t)
                        }
                        avoidanceList = _data.getAvoidanceType()
                        recyclerViewAvoidance = binding.recyclerAvoidance
                        recyclerViewAvoidance?.layoutManager =
                            StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.HORIZONTAL)
                        avoidanceAdapter = AvoidanceAdapter(dataList = avoidanceList)
                        recyclerViewAvoidance?.adapter = avoidanceAdapter

                        acceptableList = _data.getAcceptable()
                        recyclerViewAcceptable = binding.recyclerAcceptable
                        recyclerViewAcceptable?.layoutManager =
                            StaggeredGridLayoutManager(3,
                                StaggeredGridLayoutManager.HORIZONTAL)
                        acceptableAdapter = AcceptableAdapter(dataList = acceptableList)
                        recyclerViewAcceptable?.adapter = acceptableAdapter

                        _data.setAvoidanceChange(true)
                        // Roulette
                        // 在这里设置修改avoidanceList和acceptableList
                        /*
                        t?.let {
                            Kernel.delAvoidance(it)
                            Kernel.addAcceptable(it)
                        }
                         */
                        // _data.setMenuChange(true)
                        // cuisineMenu = Kernel.getResult()
                        // _data.setTodayMenu(cuisineMenu!!)
                    }
                }
            }
        }
        // Roulette Acceptable
        // 把后面的List换成获取忌口清单的函数，要求返回值为List<String>
        acceptableList = _data.getAcceptable()
        recyclerViewAcceptable = binding.recyclerAcceptable
        recyclerViewAcceptable?.layoutManager =
            StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.HORIZONTAL)
        acceptableAdapter = AcceptableAdapter(dataList = acceptableList)
        recyclerViewAcceptable?.adapter = acceptableAdapter
        acceptableAdapter?.mOnRecyclerViewItemClick = object :
            AcceptableAdapter.OnRecyclerViewItemClick<String> {
            override fun onItemClick(view: View?, t: String?, position: Int) {
                when (view?.id) {
                    R.id.content_acceptable -> {
                        t?.let {
                            _data.addAvoidance(t)
                            // Log.d("click::",t.toString())
                        }

                        avoidanceList = _data.getAvoidanceType()
                        recyclerViewAvoidance = binding.recyclerAvoidance
                        recyclerViewAvoidance?.layoutManager =
                            StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.HORIZONTAL)
                        avoidanceAdapter = AvoidanceAdapter(dataList = avoidanceList)
                        recyclerViewAvoidance?.adapter = avoidanceAdapter

                        acceptableList = _data.getAcceptable()
                        recyclerViewAcceptable = binding.recyclerAcceptable
                        recyclerViewAcceptable?.layoutManager =
                            StaggeredGridLayoutManager(3,
                                StaggeredGridLayoutManager.HORIZONTAL)
                        acceptableAdapter = AcceptableAdapter(dataList = acceptableList)
                        recyclerViewAcceptable?.adapter = acceptableAdapter

                        _data.setAvoidanceChange(true)
                        // Roulette
                        // 在这里设置修改acceptableList和acceptableList
                        /*
                        t?.let {
                            Kernel.delAcceptable(it)
                            Kernel.addAvoidance(it)
                         */
                    }
                }
            }
        }

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

        buttonLog.setOnClickListener {
            val act : FragmentActivity? =getActivity()
            Log.d("日志","点击成功")
            val intent:Intent = Intent(act, LogActivity::class.java)
            act?.startActivity(intent)

        }

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