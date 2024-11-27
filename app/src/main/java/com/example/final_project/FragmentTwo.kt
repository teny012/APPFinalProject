package com.example.final_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentTwo : Fragment() {

    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvBMR: TextView
    private lateinit var tvTDEE: TextView
    private lateinit var btnStart: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spinnerActivityLevel: Spinner
    private var userData = UserData() //使用者資料物件


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 需先填充 Layout 佈局
        val view = inflater.inflate(R.layout.activity_fragment_two, container, false)

        // 初始化 SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        //使用這個view來查找並綁定元件
        tvGender = view.findViewById<TextView>(R.id.tvGender)
        tvAge = view.findViewById<TextView>(R.id.tvAge)
        tvHeight = view.findViewById<TextView>(R.id.tvHeight)
        tvWeight = view.findViewById<TextView>(R.id.tvWeight)
        btnStart = view.findViewById<Button>(R.id.btnStart)
        tvBMR = view.findViewById<TextView>(R.id.tvBMR)
        tvTDEE = view.findViewById<TextView>(R.id.tvTDEE)
        spinnerActivityLevel = view.findViewById<Spinner>(R.id.spActivityLevel)

        // 加载并显示初始数据
        loadData()

        // 設置 Spinner 的下拉選單選項
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activity_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerActivityLevel.adapter = adapter
        }

        // 當點擊計算 計算 按鈕時
        btnStart.setOnClickListener {
            // 计算 BMR
            var userBMR = userData.calculateBMR()
            tvBMR.text = String.format("%.2f", userBMR)

            var userTDEE = userData.calculateTDEE()
            tvTDEE.text = String.format("%.2f", userTDEE)

        }

        // 當活動強度選項被選擇時，更新活動系數
        spinnerActivityLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val activityLevel = when (position) {
                    0 -> 1.2   // 久坐
                    1 -> 1.375 // 輕度活動
                    2 -> 1.55  // 中度活動
                    3 -> 1.725 // 高度活動
                    4 -> 1.9   // 非常活躍
                    else -> 1.2 // 默認為久坐
                }

                // 更新 UserData 的活動系數
                userData.updateActivityLevel(activityLevel)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 當沒有選擇時，不進行操作
            }
        }




        // 返回 View 對象
        return view
    }

    // 加载数据并更新 UI
    fun updateData() {
        loadData()
    }

    fun loadData() {
        // 从 SharedPreferences 中获取数据
        val gender = sharedPreferences.getString("gender", "")?.trim() ?: "尚未輸入"
        val age = sharedPreferences.getString("age", "0")?.toIntOrNull() ?: 0
        val height = sharedPreferences.getString("height", "0")?.toIntOrNull() ?: 0
        val weight = sharedPreferences.getString("weight", "0")?.toIntOrNull() ?: 0

        // 更新 TextView
        tvGender.text = gender
        tvAge.text = "$age"
        tvHeight.text = "$height"
        tvWeight.text = "$weight"

        // 更新 userData 的資料
        userData.updateUserData(gender, age, height, weight)

        // 打印輸出的日誌來檢查數值是否正確
        println("Gender: $gender, Age: $age, Height: $height, Weight: $weight")
        println("Calculated BMR: ${userData.calculateBMR()}")
    }







}