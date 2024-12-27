package com.example.final_project

data class FoodData(
    val food_name: String,
    val nf_calories: Float,
    val nf_total_fat: Float,
    val nf_saturated_fat: Float,
    val nf_cholesterol: Float,
    val nf_sodium: Float,
    val nf_total_carbohydrate: Float,
    val nf_dietary_fiber: Float,
    val nf_sugars: Float,
    val nf_protein: Float
)

data class NutritionResponse(
    val foods: List<FoodData>
)