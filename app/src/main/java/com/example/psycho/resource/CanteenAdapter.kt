package com.example.psycho.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.psycho.R
/*
interface OnItemClickListener {
    fun onItemClick(pos: Int)
}
 */
class CanteenAdapter(var dataList:Array<String>?):Adapter<CanteenAdapter.CanteenViewHolder>(){

    var mOnRecyclerViewItemClick: OnRecyclerViewItemClick<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanteenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.canteen_item,parent,false)
        return CanteenViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }


    override fun onBindViewHolder(holder: CanteenViewHolder, position: Int) {
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
    class CanteenViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tvContent: Button = itemView.findViewById(R.id.content_canteen)
    }

}
