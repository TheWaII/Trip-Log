package com.example.protokoll

import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogItemAdapter(private val proList: List<LogItem>) :
    RecyclerView.Adapter<LogItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logItemTextView: TextView = itemView.findViewById(R.id.logItemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = proList[position]
        val logItemFormat = holder.itemView.context.getString(R.string.log_item_format)
        val formattedText = String.format(
            logItemFormat,
            currentItem.date,
            currentItem.kmInit,
            currentItem.kmEnd,
            currentItem.kfz,
            currentItem.timeOfDay,
            currentItem.route,
            currentItem.condition
        )
        holder.logItemTextView.text = formattedText
    }


    override fun getItemCount() = proList.size
}