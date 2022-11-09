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
import com.example.psycho.databinding.FragmentDashboardBinding
import com.example.psycho.ui.log.LogActivity
import com.example.psycho.ui.log.SelectFoodActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            act?.startActivity(intent)
        }
        chooseLunch.setOnClickListener{
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            act?.startActivity(intent)
        }
        chooseDinner.setOnClickListener{
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            act?.startActivity(intent)
        }
        chooseDessert.setOnClickListener{
            val act: FragmentActivity? = getActivity()
            // Russian
            val intent: Intent = Intent(act, SelectFoodActivity::class.java)
            act?.startActivity(intent)
        }

        val textTodayBreakfast: TextView = binding.includeDailyShower.textBreakfastToday
        val textTodayLunch: TextView = binding.includeDailyShower.textLunchToday
        val textTodayDinner: TextView = binding.includeDailyShower.textDinnerToday
        val textTodayDessert: TextView = binding.includeDailyShower.textDessertToday

        return binding.root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}