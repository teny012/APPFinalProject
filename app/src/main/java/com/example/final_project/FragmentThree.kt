package com.example.final_project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class FragmentThree : Fragment() {

    private lateinit var tvWeight: TextView
    private lateinit var tvBMR: TextView
    private lateinit var tvTDEE: TextView
    private lateinit var edActivity: EditText
    private lateinit var edDuration: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvResponse: TextView
    private lateinit var userData: UserData

    private val client = OkHttpClient()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 需先填充 Layout 佈局
        val view = inflater.inflate(R.layout.activity_fragment_three, container, false)

        // 綁定 UI 元件
        tvWeight = view.findViewById(R.id.tvWeight)
        tvBMR = view.findViewById(R.id.tvBMR)
        tvTDEE = view.findViewById(R.id.tvTDEE)
        edActivity = view.findViewById(R.id.edActivity)
        edDuration = view.findViewById(R.id.edDuration)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        tvResponse = view.findViewById(R.id.tvResponse)

        // 從 SharedPreferences 中載入並顯示 userData
        loadData()

        btnSubmit.setOnClickListener {
            val activity = edActivity.text.toString()
            val weight = (userData.getUserData()["weight"].toString().toDoubleOrNull()?.times(2.2)) ?: 0
            val duration = edDuration.text.toString().toIntOrNull() ?: 60

            // 發送 API 請求
            fetchCaloriesBurned(activity, weight, duration)

        }



        // 返回 View 對象
        return view
    }

    private fun fetchCaloriesBurned(activity: String, weight: Number, duration: Int) {
        val apiKey = "YOU_API_KEY"  // 請替換成你自己的 API 金鑰
        val url = "https://api.api-ninjas.com/v1/caloriesburned?activity=$activity&weight=$weight&duration=$duration"

        // 使用 OkHttp 發送 HTTP 請求
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Api-Key", apiKey)
            .build()

        Thread {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = response.body?.string()

                    // 使用 Gson 解析 JSON 陣列
                    val gson = Gson()
                    val result = gson.fromJson(json, Array<CaloriesBurnedResult>::class.java)

                    // 更新 UI 必須在主執行緒中進行
                    requireActivity().runOnUiThread {
                        val displayText = result.joinToString(separator = "\n") {
                            "Activity: ${it.name}\nCalories/Hour: ${it.calories_per_hour}\nDuration: ${it.duration_minutes} mins\nTotal Calories: ${it.total_calories}"
                        }
                        tvResponse.text = displayText
                    }
                } else {
                    requireActivity().runOnUiThread {
                        tvResponse.text = "Failed to fetch data"
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    tvResponse.text = "Error: ${e.message}"
                }
            }
        }.start()
    }

    //刷新資料
    fun loadData() {
        // 初始化 SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("userData", null)

        if (userDataJson != null) {
            // 使用 Gson 將 JSON 轉換回 UserData 物件
            val gson = Gson()
            userData = gson.fromJson(userDataJson, UserData::class.java)
            userData.calculateTDEE()
            // 顯示體重、BMR、TDEE
            tvWeight.text = "體重: ${userData.getUserData()["weight"]} kg"
            tvBMR.text = "BMR: ${String.format("%.2f", userData.getUserData()["BMR"])}"
            tvTDEE.text = "TDEE: ${String.format("%.2f", userData.getUserData()["TDEE"])}"
        }
    }




}