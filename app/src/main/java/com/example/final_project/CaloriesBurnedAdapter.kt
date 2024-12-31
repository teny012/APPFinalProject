package com.example.final_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CaloriesBurnedAdapter(
    private var data: List<CaloriesBurnedResult>,
    private val clickListener: (CaloriesBurnedResult) -> Unit // 點擊監聽器
    ) : RecyclerView.Adapter<CaloriesBurnedAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvActivity: TextView = itemView.findViewById(R.id.tvActivity)
        val tvCaloriesPerHour: TextView = itemView.findViewById(R.id.tvCaloriesPerHour)
        val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calories_burned, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.tvActivity.text = item.name
        holder.tvCaloriesPerHour.text = "每小時消耗熱量： ${item.calories_per_hour}"
        holder.tvCalories.text = "總消耗熱量： ${item.total_calories}"

        // 設置點擊事件
        holder.itemView.setOnClickListener {
            clickListener(item) // 將點擊事件傳遞回 Fragment
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newData: List<CaloriesBurnedResult>) {
        this.data = newData
        notifyDataSetChanged()
    }
}