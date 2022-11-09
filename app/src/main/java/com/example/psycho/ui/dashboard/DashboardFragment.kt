package com.example.psycho.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.psycho.R
import com.example.psycho.data.Data
import com.example.psycho.databinding.FragmentDashboardBinding
import com.example.psycho.ui.log.LogActivity
import com.example.psycho.ui.log.SelectFoodActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val userData = Data
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val chooseBreakfast: ImageView = binding.includeDailyShower.imageBgBreakfastBorder
        val chooseLunch: ImageView = binding.includeDailyShower.imageBgLunchBorder
        val chooseDinner: ImageView = binding.includeDailyShower.imageBgDinnerBorder
        val chooseDessert: ImageView = binding.includeDailyShower.imageBgDessertBorder

        chooseBreakfast.setOnClickListener {
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            intent.putExtra("meal",1)
            act?.startActivity(intent)
        }
        chooseLunch.setOnClickListener{
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            intent.putExtra("meal",2)
            act?.startActivity(intent)
        }
        chooseDinner.setOnClickListener{
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            intent.putExtra("meal",3)
            act?.startActivity(intent)
        }
        chooseDessert.setOnClickListener{
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            intent.putExtra("meal",4)
            act?.startActivity(intent)
        }



        return binding.root

    }

    override fun onResume() {
        super.onResume()
        val textTodayBreakfast: TextView = binding.includeDailyShower.textBreakfastToday
        val textTodayLunch: TextView = binding.includeDailyShower.textLunchToday
        val textTodayDinner: TextView = binding.includeDailyShower.textDinnerToday
        val textTodayDessert: TextView = binding.includeDailyShower.textDessertToday
        val todayCalorie: TextView = binding.textTodayKal


        if(userData.checkDietLog()){
            var breakfast:List<String> = userData.getDietLog(1)
            var lunch:List<String> = userData.getDietLog(2)
            var dinner:List<String> = userData.getDietLog(3)
            var snack:List<String> = userData.getDietLog(4)

            if(breakfast.size > 0){
                textTodayBreakfast.setText(breakfast.toString())
            }
            if(lunch.size > 0){
                textTodayLunch.setText(lunch.toString())
            }
            if(dinner.size > 0){
                textTodayDinner.setText(dinner.toString())
            }
            if(snack.size > 0){
                textTodayDessert.setText(snack.toString())
            }
            val Todaycal:Int = (breakfast.size + lunch.size + dinner.size + snack.size)*180 + 130
            todayCalorie.setText(Todaycal.toString())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}