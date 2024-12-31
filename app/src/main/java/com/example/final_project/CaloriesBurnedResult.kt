package com.example.final_project

//由NINJA API傳回數據
data class CaloriesBurnedResult(
    val name: String,
    val calories_per_hour: Double,
    val duration_minutes: Int,
    val total_calories: Double
)