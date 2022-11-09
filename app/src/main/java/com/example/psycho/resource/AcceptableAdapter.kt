package com.example.psycho.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.psycho.R


class AcceptableAdapter(var dataList:List<String>?):
    RecyclerView.Adapter<AcceptableAdapter.AcceptableViewHolder>(){

    var mOnRecyclerViewItemClick: OnRecyclerViewItemClick<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_acceptable,parent,false)
        return AcceptableViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }


    override fun onBindViewHolder(holder: AcceptableViewHolder, position: Int) {
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
    class AcceptableViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tvContent: Button = itemView.findViewById(R.id.content_acceptable)
    }

}
