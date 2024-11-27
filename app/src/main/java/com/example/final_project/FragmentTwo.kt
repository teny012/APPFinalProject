package com.example.final_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentTwo : Fragment() {

    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userData: UserData


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

        // 加载并显示初始数据
        loadData()

        var gender = tvGender.text.toString()
//        var age = tvAge.text.toString().toInt()
//        var height = tvHeight.toString().toDouble()
//        var weight = tvWeight.toString().toDouble()



       // userData = UserData(gender, age, height, weight, 0.0 )


        // 返回 View 對象
        return view
    }


    // 加载数据并更新 UI
    fun updateData() {
        loadData()
    }

    fun loadData() {
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        tvGender.text = sharedPreferences.getString("gender", "尚未輸入")
        tvAge.text = "${sharedPreferences.getString("age", "尚未輸入")} 歲"
        tvHeight.text = "${sharedPreferences.getString("height", "尚未輸入")} cm"
        tvWeight.text = "${sharedPreferences.getString("weight", "尚未輸入")} kg"
    }




}