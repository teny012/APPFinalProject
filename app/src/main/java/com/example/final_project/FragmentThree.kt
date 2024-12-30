package com.example.final_project

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class FragmentThree : Fragment() {

    private lateinit var tvWeight: TextView
    private lateinit var tvTDEE: TextView
    private lateinit var tvCalories: TextView
    private lateinit var edActivity: EditText
    private lateinit var edDuration: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnCustom: Button
    private lateinit var btnShowRecord: Button
    private lateinit var userData: UserData

    private var todayCalories = 0.0

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CaloriesBurnedAdapter

    private val client = OkHttpClient()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 需先填充 Layout 佈局
        val view = inflater.inflate(R.layout.activity_fragment_three, container, false)

        // 綁定 UI 元件
        tvWeight = view.findViewById(R.id.tvWeight)
        tvTDEE = view.findViewById(R.id.tvTDEE)
        tvCalories = view.findViewById(R.id.tvCalories)
        edActivity = view.findViewById(R.id.edActivity)
        edDuration = view.findViewById(R.id.edDuration)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnCustom = view.findViewById(R.id.btnCustom)
        btnShowRecord = view.findViewById(R.id.btnShowRecord)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // 设置线性布局管理器

        // 初始化适配器并绑定到 RecyclerView
        adapter = CaloriesBurnedAdapter(emptyList()){ item ->
            // 當使用者點擊項目時，彈出 AlertDialog
            showConfirmationDialog(item)
        }
        recyclerView.adapter = adapter

        // 從 SharedPreferences 中載入並顯示 userData
        loadData()

        //送出request
        btnSubmit.setOnClickListener {
            if (!::userData.isInitialized) {
                // 如果 userData 尚未初始化，提示錯誤訊息
                Toast.makeText(requireContext(), "使用者資料尚未加載", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val activity = edActivity.text.toString()
            val weight = (userData.getUserData().get("weight").toString().toDoubleOrNull()?.times(2.2)) ?: 0
            val duration = edDuration.text.toString().toIntOrNull() ?: 60

            if(activity.isEmpty() || weight == 0.0 || duration == 0){
                Toast.makeText(requireContext(), "請輸入正確的資料", Toast.LENGTH_SHORT).show()
            }
            else{
                // 發送 API 請求
                fetchCaloriesBurned(activity, weight, duration)
                Toast.makeText(requireContext(), "已送出", Toast.LENGTH_SHORT).show()
            }
        }

        //顯示運動紀錄
        btnShowRecord.setOnClickListener {
            showRecordDialog() // 點擊按鈕後，顯示已添加的紀錄
        }

        btnCustom.setOnClickListener {
            customExercise()
        }



        // 返回 View 對象
        return view
    }

    //使用者自訂運動
    private fun customExercise() {
        val sharedPreferences = requireActivity().getSharedPreferences("ActivityRecords", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_custom_exercise, null)

        val edExerciseName = dialogView.findViewById<EditText>(R.id.edExerciseName)
        val edExerciseCalories = dialogView.findViewById<EditText>(R.id.edExerciseCalories)

        builder.setView(dialogView)
            .setPositiveButton("新增") { dialog, _ ->
                val exerciseName = edExerciseName.text.toString()
                val exerciseCalories = edExerciseCalories.text.toString().toDoubleOrNull()
                val customCaloriesBurnedResult = CaloriesBurnedResult(exerciseName, 0.0, 0, exerciseCalories ?: 0.0)
                addToRecord(customCaloriesBurnedResult)
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss()}

        val dialog = builder.create()
        dialog.show()


    }

    private fun showRecordDialog() {
        val sharedPreferences = requireActivity().getSharedPreferences("ActivityRecords", Context.MODE_PRIVATE)
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

                todayCalories -= record.split(":")[2].trim().toDouble()
                tvCalories.text = "今日消耗熱量： $todayCalories"
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


    private fun showConfirmationDialog(item: CaloriesBurnedResult) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("加入紀錄")
        builder.setMessage("是否將${item.name}加入紀錄？")

        // 點擊「是」按鈕的事件
        builder.setPositiveButton("是") { dialog, _ ->
            // 在這裡處理將該項目添加到紀錄的邏輯
            if(addToRecord(item))
                Toast.makeText(requireContext(), "${item.name} 已加入紀錄", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // 點擊「否」按鈕的事件
        builder.setNegativeButton("否") { dialog, _ ->
            dialog.dismiss() // 取消操作
        }

        // 顯示對話框
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // 將項目添加到紀錄的方法
    private fun addToRecord(item: CaloriesBurnedResult): Boolean {
        // 這裡可以將該項目保存到紀錄中（例如，SharedPreferences、資料庫等）
        // 示例：保存到 SharedPreferences 或自定義的紀錄數據結構
        val sharedPreferences = requireActivity().getSharedPreferences("ActivityRecords", Context.MODE_PRIVATE)
        val allRecords = sharedPreferences.all

        // 檢查是否已經有相同的活動紀錄
        if (allRecords.values.any { it.toString().contains(item.name) }) {
            Toast.makeText(requireContext(), "${item.name} 已經存在於紀錄中", Toast.LENGTH_SHORT).show()
            return false // 如果已經存在，返回 false
        }

        val editor = sharedPreferences.edit()

        // 使用活動名稱和當前時間戳組合成唯一的 key，避免重複的項目被覆蓋
        val uniqueKey = "${item.name}_${System.currentTimeMillis()}"

        // 更新 todayCalories 並顯示
        todayCalories += item.total_calories
        tvCalories.text = "今日消耗熱量： $todayCalories"

        // 保存當前的今日消耗熱量到 SharedPreferences
        editor.putFloat("todayCalories", todayCalories.toFloat())

        // 將紀錄轉換為字符串格式保存，這裡簡單示例保存活動名稱
        editor.putString(uniqueKey, "活動: ${item.name}\n總消耗熱量: ${item.total_calories}")
        editor.apply()

        return true // 如果成功添加，返回 true
    }

    //向API Ninjas發送請求
    private fun fetchCaloriesBurned(activity: String, weight: Number, duration: Int) {
        val apiKey = "bb9Ja8ikDneisbrxUPpsKA==Gzfei2Pl2K5cqg4j"  // 請替換成你自己的 API 金鑰
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
                        // 更新 RecyclerView 的適配器資料
                        adapter.updateData(result.toList())
                    }
                } else {
                    requireActivity().runOnUiThread {
                        // Handle error
                        Toast.makeText(requireContext(), "請求失敗，請再試一次", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    // Handle exception
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    //刷新資料
    fun loadData() {
        // 初始化 SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("userData", null)

        // 從 SharedPreferences2 中讀取上次保存的 todayCalories，默認值為 0
        val sharedPreferences2 = requireActivity().getSharedPreferences("ActivityRecords", Context.MODE_PRIVATE)
        todayCalories = sharedPreferences2.getFloat("todayCalories", 0f).toDouble()

        tvCalories.text = "今日消耗熱量： $todayCalories"


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