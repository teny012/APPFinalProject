package com.example.final_project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class FragmentThree : Fragment() {

    private lateinit var tvWeight: TextView
    private lateinit var tvBMR: TextView
    private lateinit var tvTDEE: TextView



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

        // 從 SharedPreferences 中載入並顯示 userData
        loadData()



        // 返回 View 對象
        return view
    }

    fun loadData() {
        // 初始化 SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userDataJson = sharedPreferences.getString("userData", null)

        if (userDataJson != null) {
            // 使用 Gson 將 JSON 轉換回 UserData 物件
            val gson = Gson()
            val userData = gson.fromJson(userDataJson, UserData::class.java)
            userData.calculateTDEE()
            // 顯示體重、BMR、TDEE
            tvWeight.text = "體重: ${userData.getUserData()["weight"]} kg"
            tvBMR.text = "BMR: ${String.format("%.2f", userData.getUserData()["BMR"])}"
            tvTDEE.text = "TDEE: ${String.format("%.2f", userData.getUserData()["TDEE"])}"
        }
    }




}