package com.example.final_project

class UserData {
    //成員變數
    private var gender: String = "男"
    private var age: Int = 0
    private var height: Int = 0 // 身高（单位：厘米）
    private var weight: Int = 0 // 体重（单位：公斤）
    private var activityLevel: Double = 1.2 // 活动系数
    private var BMR: Double = 0.0;
    private var TDEE: Double = 0.0;





    // BMR 计算 (基础代谢率)
    fun calculateBMR(): Double {
        return if (gender == "男") {
            66 + (13.7 * weight) + (5 * height) - (6.8 * age)
        } else {
            655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
        }
    }

    // TDEE 计算 (总消耗热量)
    fun calculateTDEE(): Double {
        val bmr = calculateBMR()
        return bmr * activityLevel
    }

    // 更新用户资料
    fun updateUserData(newGender: String, newAge: Int, newHeight: Int, newWeight: Int) {
        if (newAge <= 0 || newHeight <= 0 || newWeight <= 0) {
            throw IllegalArgumentException("請輸入正確數值")
        }
        gender = newGender
        age = newAge
        height = newHeight
        weight = newWeight
    }

    // 更新活動係數
    fun updateActivityLevel(newActivityLevel: Double){
        activityLevel = newActivityLevel
    }

    fun updateBMR(newBMR: Double){
        BMR = newBMR
    }

    fun updateTDEE(newTDEE: Double){
        TDEE = newTDEE
    }

    // 获取用户资料
    fun getUserData(): Map<String, Any> {
        return mapOf(
            "gender" to gender,
            "age" to age,
            "height" to height,
            "weight" to weight,
            "activityLevel" to activityLevel,
            "BMR" to BMR,
            "TDEE" to TDEE
        )
    }
}
