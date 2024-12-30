package com.example.final_project

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


class FragmentFour : Fragment() {

    private lateinit var tvWeight: TextView
    private lateinit var tvTDEE: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvFood: TextView
    private lateinit var edFood: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnShowRecord: Button
    private lateinit var btnCustom: Button
    private lateinit var btnAdd: Button

    private lateinit var userData: UserData

    private var todayCalories = 0.0

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 需先填充 Layout 佈局
        val view = inflater.inflate(R.layout.activity_fragment_four, container, false)

        tvWeight = view.findViewById(R.id.tvWeight)
        tvTDEE = view.findViewById(R.id.tvTDEE)
        tvCalories = view.findViewById(R.id.tvCalories)
        tvFood = view.findViewById(R.id.tvFood)
        edFood = view.findViewById(R.id.edFood)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnShowRecord = view.findViewById(R.id.btnShowRecord)
        btnCustom = view.findViewById(R.id.btnCustom)
        btnAdd = view.findViewById(R.id.btnAdd)

        // 從 SharedPreferences 中載入並顯示 userData
        loadData()

        //送出request
        btnSubmit.setOnClickListener { 
            if (!::userData.isInitialized) {
                // 如果 userData 尚未初始化，提示錯誤訊息
                Toast.makeText(requireContext(), "使用者資料尚未加載", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if(edFood.text.isEmpty()){
                Toast.makeText(requireContext(), "請輸入正確的資料", Toast.LENGTH_SHORT).show()
            }
            else{
                // 發送 API 請求
                fetchFoodCalories(edFood.text.toString())
                Toast.makeText(requireContext(), "已送出", Toast.LENGTH_SHORT).show()

            }
        }

        //加入食物紀錄
        btnAdd.setOnClickListener {
            if (addToRecord()) {
                Toast.makeText(requireContext(), "已加入紀錄", Toast.LENGTH_SHORT).show()
            }
        }

        //顯示食物紀錄
        btnShowRecord.setOnClickListener {
            showRecordDialog() // 點擊按鈕後，顯示已添加的紀錄
        }

        btnCustom.setOnClickListener {
            customFood()
        }














        return view
    }

    //使用者自訂食物
    private fun customFood() {
        val sharedPreferences = requireActivity().getSharedPreferences("FoodRecords", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_custom_food, null)

        val edFoodName = dialogView.findViewById<EditText>(R.id.edFoodName)
        val edFoodCalories = dialogView.findViewById<EditText>(R.id.edFoodCalories)
        val edFoodTotalFat = dialogView.findViewById<EditText>(R.id.edFoodTotalFat)
        val edFoodSaturatedFat = dialogView.findViewById<EditText>(R.id.edFoodSaturatedFat)
        val edFoodCholesterol = dialogView.findViewById<EditText>(R.id.edFoodCholesterol)
        val edFoodSodium = dialogView.findViewById<EditText>(R.id.edFoodSodium)
        val edFoodCarbohydrate = dialogView.findViewById<EditText>(R.id.edFoodCarbohydrate)
        val edFoodDietaryFiber = dialogView.findViewById<EditText>(R.id.edFoodDietaryFiber)
        val edFoodSugars = dialogView.findViewById<EditText>(R.id.edFoodSugars)
        val edFoodProtein = dialogView.findViewById<EditText>(R.id.edFoodProtein)

        builder.setView(dialogView)
            .setPositiveButton("新增") { dialog, _ ->
                val foodName = edFoodName.text.toString()
                val foodCalories = edFoodCalories.text.toString().toDoubleOrNull()
                val foodTotalFat = edFoodTotalFat.text.toString().toDoubleOrNull()
                val foodSaturatedFat = edFoodSaturatedFat.text.toString().toDoubleOrNull()
                val foodCholesterol = edFoodCholesterol.text.toString().toDoubleOrNull()
                val foodSodium = edFoodSodium.text.toString().toDoubleOrNull()
                val foodCarbohydrate = edFoodCarbohydrate.text.toString().toDoubleOrNull()
                val foodDietaryFiber = edFoodDietaryFiber.text.toString().toDoubleOrNull()
                val foodSugars = edFoodSugars.text.toString().toDoubleOrNull()
                val foodProtein = edFoodProtein.text.toString().toDoubleOrNull()

                val customFoodData = FoodData(foodName, foodCalories ?: 0.0, foodTotalFat ?: 0.0, foodSaturatedFat ?: 0.0,foodCholesterol ?: 0.0,
                    foodSodium ?: 0.0, foodCarbohydrate ?: 0.0, foodDietaryFiber ?: 0.0, foodSugars ?: 0.0, foodProtein ?: 0.0)
                addToRecord(customFoodData)
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss()}

        val dialog = builder.create()
        dialog.show()
    }

    private fun showRecordDialog() {
        val sharedPreferences = requireActivity().getSharedPreferences("FoodRecords", Context.MODE_PRIVATE)
        val allRecords = sharedPreferences.all.filterNot { it.key == "todayCalories" } // 過濾掉 "todayCalories" 項目

        if (allRecords.isEmpty()) {
            Toast.makeText(requireContext(), "沒有紀錄", Toast.LENGTH_SHORT).show()
            return
        }

        // 創建一個 ArrayList 來儲存所有活動紀錄
        val recordList = ArrayList<String>()
        for ((_, value) in allRecords) {
            recordList.add(value.toString()) // 添加紀錄到 ArrayList
        }

        // 使用 AlertDialog.Builder 創建一個包含 ListView 的對話框
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_record_list, null)
        val listView = dialogView.findViewById<ListView>(R.id.listView)


        // 設置 ListView 的適配器來顯示紀錄
        val adapter = RecordAdapter(requireContext(), recordList)
        listView.adapter = adapter

        // 當點擊 ListView 項目時，刪除該活動紀錄
        listView.setOnItemClickListener { _, _, position, _ ->
            val record = recordList[position]
            val keyToRemove = allRecords.filterValues { it == record }.keys.firstOrNull()

            if (keyToRemove != null) {
                // 刪除該項紀錄
                val editor = sharedPreferences.edit()
                editor.remove(keyToRemove)

                todayCalories -= record.substringAfter("熱量：").substringBefore(" kcal").toDouble()
                tvCalories.text = "今日攝取熱量： ${String.format("%.2f", todayCalories)}"
                editor.putFloat("todayCalories", todayCalories.toFloat())
                editor.apply()



                // 從顯示列表中刪除該項
                recordList.removeAt(position)
                adapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), "紀錄已刪除", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(dialogView)
            .setPositiveButton("關閉") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun addToRecord(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("FoodRecords", Context.MODE_PRIVATE)
        val allRecords = sharedPreferences.all

        // 檢查是否已經有相同的食物紀錄
        if (allRecords.values.any { it.toString().contains(tvFood.text.toString()) }) {
            Toast.makeText(requireContext(), "已經存在於紀錄中", Toast.LENGTH_SHORT).show()
            return false// 如果已經存在，返回 false
        }

        val editor = sharedPreferences.edit()
        val foodName = tvFood.text.toString().substringAfter("名稱：").substringBefore("熱量：")

        // 使用活動名稱和當前時間戳組合成唯一的 key，避免重複的項目被覆蓋
        val uniqueKey = "${foodName}_${System.currentTimeMillis()}"
        editor.putString(uniqueKey, tvFood.text.toString())

        // 更新 todayCalories 並顯示和保存
        todayCalories += tvFood.text.toString().substringAfter("熱量：").substringBefore(" kcal").toDouble()
        tvCalories.text = "今日攝取熱量： ${String.format("%.2f", todayCalories)}"

        // 更新 todayCalories 並保存
        editor.putFloat("todayCalories", todayCalories.toFloat())
        editor.apply()

        return true
    }

    //addToRecord多載，以區分是否為自訂食物
    private fun addToRecord(item : FoodData): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("FoodRecords", Context.MODE_PRIVATE)
        val allRecords = sharedPreferences.all

        // 檢查是否已經有相同的食物紀錄
        if (allRecords.values.any { it.toString().contains(item.food_name) }) {
            Toast.makeText(requireContext(), "已經存在於紀錄中", Toast.LENGTH_SHORT).show()
            return false// 如果已經存在，返回 false
        }

        val editor = sharedPreferences.edit()
        val foodName = item.food_name

        // 使用活動名稱和當前時間戳組合成唯一的 key，避免重複的項目被覆蓋
        val uniqueKey = "${foodName}_${System.currentTimeMillis()}"
        editor.putString(uniqueKey, "食物名稱：${item.food_name}\n" +
                "熱量：${item.nf_calories} kcal\n"+
                "脂肪：${item.nf_total_fat} g\n"+
                "飽和脂肪：${item.nf_saturated_fat} g\n"+
                "膽固醇：${item.nf_cholesterol} mg\n"+
                "鈉：${item.nf_sodium} mg\n"+
                "碳水化合物：${item.nf_total_carbohydrate} g\n"+
                "膳食纖維：${item.nf_dietary_fiber} g\n"+
                "糖分：${item.nf_sugars} g\n"+
                "蛋白質：${item.nf_protein} g\n")

        // 更新 todayCalories 並顯示和保存
        todayCalories += item.nf_calories
        tvCalories.text = "今日攝取熱量： ${String.format("%.2f", todayCalories)}"

        // 更新 todayCalories 並保存
        editor.putFloat("todayCalories", todayCalories.toFloat())
        editor.apply()

        return true
    }

    // 向 Nutritionix API 發送請求
    private fun fetchFoodCalories(toString: String) {
        val foodQuery = edFood.text.toString()

        // 準備要發送的 JSON body
        val json = """{"query": "$foodQuery"}"""
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json
        )

        // 建立 OkHttp 請求
        val request = Request.Builder()
            .url("https://trackapi.nutritionix.com/v2/natural/nutrients")
            .post(requestBody)
            .addHeader("x-app-id", "4dab2cb4")
            .addHeader("x-app-key", "1d105b81fb9046bed1912543bc410619")
            .addHeader("Content-Type", "application/json")
            .build()

        //發送請求
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 如果請求失敗，顯示錯誤訊息
                activity?.runOnUiThread {
                    tvFood.text = "請求失敗: ${e.message}"
                    btnAdd.visibility = View.INVISIBLE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                // 解析回應
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // 使用 Gson 解析 JSON 並匹配到 data class
                    val gson = Gson()
                    val nutritionResponse = gson.fromJson(responseBody, NutritionResponse::class.java)

                    // 假設只處理第一個食物資料
                    val foodData = nutritionResponse.foods.firstOrNull()

                    // 在 UI 執行緒中更新 TextView
                    activity?.runOnUiThread {
                        if (foodData != null) {
                            tvFood.text = """
                                        食物名稱：${foodData.food_name}
                                        熱量：${foodData.nf_calories} kcal
                                        脂肪：${foodData.nf_total_fat} g
                                        飽和脂肪：${foodData.nf_saturated_fat} g
                                        膽固醇：${foodData.nf_cholesterol} mg
                                        鈉：${foodData.nf_sodium} mg
                                        碳水化合物：${foodData.nf_total_carbohydrate} g
                                        膳食纖維：${foodData.nf_dietary_fiber} g
                                        糖分：${foodData.nf_sugars} g
                                        蛋白質：${foodData.nf_protein} g
                                    """.trimIndent()
                            btnAdd.visibility = View.VISIBLE
                        } else {
                            tvFood.text = "找不到該食物的營養資訊。"
                            btnAdd.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        tvFood.text = "查詢失敗，請稍後再試。"
                        btnAdd.visibility = View.INVISIBLE
                    }
                }
            }
        })




    }

    //刷新資料
    fun loadData() {
        // 初始化 SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("userData", null)

        //從 SharedPreferences2 中讀取上次保存的 todayCalories，默認值為 0
        val sharedPreferences2 = requireActivity().getSharedPreferences("FoodRecords", Context.MODE_PRIVATE)
        todayCalories = sharedPreferences2.getFloat("todayCalories", 0f).toDouble()

        // 顯示今天的熱量攝取
        tvCalories.text = "今日攝取熱量： ${String.format("%.2f", todayCalories)}"



        if (userDataJson != null) {
            // 使用 Gson 將 JSON 轉換回 UserData 物件
            val gson = Gson()
            userData = gson.fromJson(userDataJson, UserData::class.java)
            userData.calculateTDEE()
            // 顯示體重、BMR、TDEE
            tvWeight.text = "體重: ${userData.getUserData()["weight"]} kg"
            tvTDEE.text = "TDEE: ${String.format("%.2f", userData.getUserData()["TDEE"])}"
        }
    }

}