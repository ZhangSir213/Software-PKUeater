package com.example.psycho.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.psycho.R


class CanteenAdapter(val canteenList:Array<String>): RecyclerView.Adapter<CanteenAdapter.CanteenViewHolder>(){
    class CanteenViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val canteenButton: Button = itemView.findViewById(R.id.button_canteen)

        fun bind(word:String) {
            canteenButton.text = word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanteenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.canteen_item,parent,false)
        return CanteenViewHolder(view)
    }

    override fun getItemCount(): Int {
        return canteenList.size
    }

    override fun onBindViewHolder(
        holder: CanteenViewHolder,
        position: Int
    ) {
        holder.bind(canteenList[position])
    }
}
