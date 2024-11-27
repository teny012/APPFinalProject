package com.example.final_project

class UserData(
    private var gender: String,
    private var age: Int,
    private var height: Double, // 身高（单位：厘米）
    private var weight: Double, // 体重（单位：公斤）
    private var activityLevel: Double // 活动系数
) {

    // BMR 计算 (基础代谢率)
    fun calculateBMR(): Double {
        return if (gender == "男") {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
    }

    // TDEE 计算 (总消耗热量)
    fun calculateTDEE(): Double {
        val bmr = calculateBMR()
        return bmr * activityLevel
    }

    // 更新用户资料
    fun updateUserData(newGender: String, newAge: Int, newHeight: Double, newWeight: Double) {
        gender = newGender
        age = newAge
        height = newHeight
        weight = newWeight
    }

    // 更新活動係數
    fun updateActivityLevel(newActivityLevel: Double){
        activityLevel = newActivityLevel
    }

    // 获取用户资料
    fun getUserData(): Map<String, Any> {
        return mapOf(
            "gender" to gender,
            "age" to age,
            "height" to height,
            "weight" to weight,
            "activityLevel" to activityLevel
        )
    }
}
