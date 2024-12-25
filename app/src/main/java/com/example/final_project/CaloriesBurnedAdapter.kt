package com.example.final_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class CaloriesBurnedAdapter(
    context: Context,
    private val data: Array<CaloriesBurnedResult>
) : ArrayAdapter<CaloriesBurnedResult>(context, R.layout.list_item, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)

        val activityName = view.findViewById<TextView>(R.id.activityName)
        val caloriesPerHour = view.findViewById<TextView>(R.id.caloriesPerHour)
        val duration = view.findViewById<TextView>(R.id.duration)
        val totalCalories = view.findViewById<TextView>(R.id.totalCalories)

        val item = data[position]

        activityName.text = "運動名稱： ${item.name}"
        caloriesPerHour.text = "每小時消耗熱量： ${item.calories_per_hour} 大卡"
        duration.text = "運動時間： ${item.duration_minutes} 分鐘"
        totalCalories.text = "總消耗熱量： ${item.total_calories} 大卡"

        return view
    }
}