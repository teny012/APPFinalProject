package com.example.final_project

data class FoodData(
    val food_name: String,
    val nf_calories: Double,
    val nf_total_fat: Double,
    val nf_saturated_fat: Double,
    val nf_cholesterol: Double,
    val nf_sodium: Double,
    val nf_total_carbohydrate: Double,
    val nf_dietary_fiber: Double,
    val nf_sugars: Double,
    val nf_protein: Double
)

data class NutritionResponse(
    val foods: List<FoodData>
)