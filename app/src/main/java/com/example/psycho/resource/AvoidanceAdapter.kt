package com.example.psycho.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.psycho.R


class AvoidanceAdapter(var dataList:List<String>?):
    RecyclerView.Adapter<AvoidanceAdapter.AvoidanceViewHolder>(){

    var mOnRecyclerViewItemClick: OnRecyclerViewItemClick<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvoidanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avoidance,parent,false)
        return AvoidanceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }


    override fun onBindViewHolder(holder: AvoidanceViewHolder, position: Int) {
        var context = holder.itemView.context

        val content = dataList?.get(position).toString()
        holder.tvContent.text = content

        holder.itemView.setOnClickListener {
            mOnRecyclerViewItemClick?.onItemClick(holder.itemView, content, position)
        }
        holder.tvContent.setOnClickListener {
            mOnRecyclerViewItemClick?.onItemClick(holder.tvContent, content, position)
        }
    }
    public interface OnRecyclerViewItemClick<T> {
        fun onItemClick(view: View?, t: T?, position: Int)
    }
    class AvoidanceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tvContent: Button = itemView.findViewById(R.id.content_avoidance)
    }

}
