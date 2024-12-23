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
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

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
    private lateinit var infoIcon: ImageView
    private var userData = UserData() // 使用者資料物件

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 需先填充 Layout 佈局
        val view = inflater.inflate(R.layout.activity_fragment_two, container, false)

        // 初始化 SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // 綁定元件
        tvGender = view.findViewById(R.id.tvGender)
        tvAge = view.findViewById(R.id.tvAge)
        tvHeight = view.findViewById(R.id.tvHeight)
        tvWeight = view.findViewById(R.id.tvWeight)
        btnStart = view.findViewById(R.id.btnStart)
        tvBMR = view.findViewById(R.id.tvBMR)
        tvTDEE = view.findViewById(R.id.tvTDEE)
        spinnerActivityLevel = view.findViewById(R.id.spActivityLevel)
        infoIcon = view.findViewById(R.id.info_icon)

        // 加載並顯示資料
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

        // 設置計算按鈕的點擊事件
        btnStart.setOnClickListener {
            // 計算 BMR 和 TDEE 並更新
            val userBMR = userData.calculateBMR()
            userData.updateBMR(userBMR)

            val userTDEE = userData.calculateTDEE()
            userData.updateTDEE(userTDEE)

            // 保存最新的 userData
            saveUserData(userData)
            // 加載資料並更新 UI
            loadData()


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
                userData.updateActivityLevel(activityLevel)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 當沒有選擇時，不進行操作
            }
        }

        // info_icon 的點擊事件，顯示 BottomSheetDialog
        infoIcon.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomSheetDialog.setContentView(dialogView)
            bottomSheetDialog.show()
        }

        return view
    }

    // 加載資料並更新 UI
    fun loadData() {
        val loadedUserData = loadUserData()
        if (loadedUserData != null) {
            userData = loadedUserData
        }

        // 更新 TextView 顯示
        tvGender.text = userData.getUserData()["gender"].toString()
        tvAge.text = userData.getUserData()["age"].toString()
        tvHeight.text = userData.getUserData()["height"].toString()
        tvWeight.text = userData.getUserData()["weight"].toString()

        // 如果需要，也可以重新計算 BMR/TDEE
        val userBMR = userData.calculateBMR()
        tvBMR.text = String.format("%.2f", userBMR)
        val userTDEE = userData.calculateTDEE()
        tvTDEE.text = String.format("%.2f", userTDEE)
    }

    // 儲存 userData 到 SharedPreferences
    private fun saveUserData(userData: UserData) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userDataJson = gson.toJson(userData)
        editor.putString("userData", userDataJson)
        editor.apply()
    }

    // 從 SharedPreferences 載入 userData
    private fun loadUserData(): UserData? {
        val userDataJson = sharedPreferences.getString("userData", null)
        return if (userDataJson != null) {
            val gson = Gson()
            gson.fromJson(userDataJson, UserData::class.java)
        } else {
            null
        }
    }


}
