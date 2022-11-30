package com.example.psycho.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.FragmentHomeBinding
import com.example.psycho.kernel.Kernel
import com.example.psycho.resource.CanteenAdapter
import com.example.psycho.resource.CanteenAdapter.OnRecyclerViewItemClick
import com.squareup.picasso.Picasso
import java.util.*

class HomeFragment : Fragment() {
    private var _data = Data

    private var _binding: FragmentHomeBinding? = null
    private var canteenList: List<String>? = null
    private var cuisineMenu: List<String>? = null
    /*
        listOf(
        "农园一层", "农园二层", "燕南一层", "家园一层",
        "家园二层", "家园三层", "家园四层", "松林包子",
        "学一食堂", "学五食堂", "勺园一层", "勺园二层",
        "佟园餐厅", "勺园西餐厅", "勺园中餐厅",
        "点外卖", "出校吃", "吃餐车")
     */
    private var recyclerViewCanteen:RecyclerView? = null
    var canteenAdapter:CanteenAdapter? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _data.setTodayMenu(Kernel.getResult())
        _data.setMenuChange(true)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val act: FragmentActivity? = activity
        // canteen 的点击事件绑定
        canteenList = Kernel.getAllCanteen()
        recyclerViewCanteen=binding.recyclerCanteen
        recyclerViewCanteen?.layoutManager = LinearLayoutManager(this.context)
        canteenAdapter = CanteenAdapter(dataList = canteenList)
        recyclerViewCanteen?.adapter = canteenAdapter
        canteenAdapter?.mOnRecyclerViewItemClick = object :OnRecyclerViewItemClick<String>{
            override fun onItemClick(view:View?, t:String?, position:Int) {
                when (view?.id) {

                    R.id.item_view_canteen -> Toast.makeText(
                        this@HomeFragment.activity,
                        "您本次选择了${position}",
                        Toast.LENGTH_SHORT
                    ).show()
                    /*
                    R.id.content_canteen -> Toast.makeText(
                        activity,
                        "点击的item是$t",
                        Toast.LENGTH_SHORT).show()
                     */
                    R.id.content_canteen -> {
                        t?.let { Kernel.setPrefer(it) }
                        _data.setMenuChange(true)
                        cuisineMenu = Kernel.getResult()
                        _data.setTodayMenu(cuisineMenu!!)
                        cuisineMenu=null
                    }
                }
                println("yes")
            }
        }
        // 点击获取今天的推荐菜品
        val buttonGetMenu: Button = binding.buttonGetMenu
        buttonGetMenu.setOnClickListener {
            val act: FragmentActivity? = activity
            cuisineMenu = Kernel.getResult()
            _data.setTodayMenu(cuisineMenu!!)
            _data.setMenuChange(true)
        }
        val buttonSetRand: Button = binding.contentCanteenRandom
        buttonSetRand.setOnClickListener {
            val act: FragmentActivity? = activity
            Kernel.setPrefer("随机食堂")
            cuisineMenu = Kernel.getResult()
            _data.setTodayMenu(cuisineMenu!!)
            _data.setMenuChange(true)
            Log.d("Random","choose")
        }
        // timer刷新重写
        val textTodayCanteen: TextView = binding.textTodayCanteen
        val textCuisine1: TextView = binding.textCuisine1
        val textCuisine2: TextView = binding.textCuisine2
        val textCuisine3: TextView = binding.textCuisine3
        val imageCuisine1:ImageView=binding.imageCuisine1
        val imageCuisine2:ImageView=binding.imageCuisine2
        val imageCuisine3:ImageView=binding.imageCuisine3
        val timer = Timer()
        val button1:Button=binding.buttonAddlog1
        val button2:Button=binding.buttonAddlog2
        val button3:Button=binding.buttonAddlog3

         button1.setOnClickListener{
             val text=textCuisine1.text
             if (text!="菜品名称")
             {
                val foodFind=Kernel.getFood(text.toString())
                 _data.postLogToServer(foodFind.id, 2)
                 Toast.makeText(context,"已添加到日志！请享用美食吧~", Toast.LENGTH_SHORT).show()
             }
         }
        button2.setOnClickListener{
            val text=textCuisine2.text
            if (text!="菜品名称")
            {
                val foodFind=Kernel.getFood(text.toString())
                _data.postLogToServer(foodFind.id, 2)
                Toast.makeText(context,"已添加到日志！请享用美食吧~", Toast.LENGTH_SHORT).show()
            }
        }
        button3.setOnClickListener{
            val text=textCuisine3.text
            if (text!="菜品名称")
            {
                val foodFind=Kernel.getFood(text.toString())
                _data.postLogToServer(foodFind.id, 2)
                Toast.makeText(context,"已添加到日志！请享用美食吧~", Toast.LENGTH_SHORT).show()
            }
        }
        timer.schedule(object : TimerTask() {
            override fun run() {
                //在这里更新数据
                activity?.runOnUiThread {
                    if(_data.getTimerFlag()) {
                        if (_data.getMenuChange()) {

                            var todayMenu = _data.getTodayMenu()

                            textTodayCanteen.text = todayMenu[0]
                            Log.d("homessss",todayMenu.size.toString())
                            textCuisine1.text = "菜品名称"
                            textCuisine2.text = "菜品名称"
                            textCuisine3.text = "菜品名称"

                            //textCuisine1.text = todayMenu[1]
                            //textCuisine2.text = todayMenu[2]
                            //textCuisine3.text = todayMenu[3]

                            //imageCuisine1.setImageResource(Kernel.getPictureId(todayMenu[1])!!)
                            //imageCuisine2.setImageResource(Kernel.getPictureId(todayMenu[2])!!)
                            //imageCuisine3.setImageResource(Kernel.getPictureId(todayMenu[3])!!)

                            textCuisine1.text = "菜品名称"
                            textCuisine2.text = "菜品名称"
                            textCuisine3.text = "菜品名称"

                            imageCuisine1.setImageResource(R.drawable.pkueater)
                            imageCuisine2.setImageResource(R.drawable.pkueater)
                            imageCuisine3.setImageResource(R.drawable.pkueater)
                            if(todayMenu.size >= 2){
                                textCuisine1.text = todayMenu[1]
                                val foodFind=Kernel.getFood(todayMenu[1])
                                Picasso.with(context).load("http://47.94.139.212:3000"+foodFind.imgAddr).into(imageCuisine1)
                                //imageCuisine1.setImageResource(Kernel.getPictureId(todayMenu[1])!!)
                            }
                            if(todayMenu.size >= 3){//有两个菜品的情况
                                textCuisine2.text = todayMenu[2]
                                val foodFind=Kernel.getFood(todayMenu[2])
                                Picasso.with(context).load("http://47.94.139.212:3000"+foodFind.imgAddr).into(imageCuisine2)
                                //imageCuisine2.setImageResource(Kernel.getPictureId(todayMenu[2])!!)
                            }else if(todayMenu.size >= 4){
                                textCuisine3.text = todayMenu[3]
                                val foodFind=Kernel.getFood(todayMenu[3])
                                Picasso.with(context).load("http://47.94.139.212:3000"+foodFind.imgAddr).into(imageCuisine3)
                                //imageCuisine3.setImageResource(Kernel.getPictureId(todayMenu[3])!!)
                            }


                        }
                    }
                }
            }
        }, 0, 500) //延迟10毫秒后，执行一次task

        //// 看一下scram模型是怎么来落实的？
        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
         */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}