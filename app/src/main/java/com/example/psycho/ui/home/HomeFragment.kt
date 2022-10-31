package com.example.psycho.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.psycho.databinding.FragmentHomeBinding
import com.example.psycho.resource.Canteen
import com.example.psycho.resource.CanteenAdapter

/**
 * added by ZhangHaoyu @ 2022/10/21
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val act: FragmentActivity? = getActivity()
        val canteenList = act?.let { Canteen(it.applicationContext).getCanteenList() }
        val recyclerCanteen:RecyclerView = binding.recyclerCanteen
        recyclerCanteen.adapter = canteenList?.let { CanteenAdapter(it) }
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}