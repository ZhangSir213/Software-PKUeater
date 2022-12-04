package com.example.psycho.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.psycho.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //val circleCpb1 = binding.circleProgressBarA
        //val circleCpb2 = binding.circleProgressBarB

        with(binding.circleProgressBarA) {
            rindColorArray = intArrayOf(    // 渐变颜色
                Color.parseColor("#0888FF"),
                Color.parseColor("#6CD0FF")
            )
            startAnim(75)
        }

        with(binding.circleProgressBarB) {
            rindColorArray = intArrayOf(
                Color.parseColor("#0888FF"),
                Color.parseColor("#6CD0FF")
            )
            startAnim(85)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}