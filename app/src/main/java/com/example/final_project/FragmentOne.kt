package com.example.final_project

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import java.util.Locale
import java.util.Date

class FragmentOne : Fragment() {

    private lateinit var radioGroupGender: RadioGroup
    private lateinit var edAge: EditText
    private lateinit var edHeight : EditText
    private lateinit var edWeight : EditText
    private lateinit var btnSave : Button
    private lateinit var btnDate: Button
    private lateinit var btnGoal: Button
    private lateinit var tvDate: TextView
    private lateinit var lvRecords : ListView
    private lateinit var recordsAdapter: ArrayAdapter<String>
    private val recordsList = mutableListOf<String>()
    private var selectedDate: String? = null
    private var selectedGender: String = "男" // 預設性別

    // 用於 SharedPreferences 儲存資料
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesUserData: SharedPreferences

    private val PREFS_NAME = "WeightDietExercisePrefs"
    private val RECORDS_KEY = "records" // 用來儲存紀錄的鍵

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 需先填充 Layout 佈局
        val view = inflater.inflate(R.layout.activity_fragment_one, container, false)
        //使用這個view來查找並綁定元件
        radioGroupGender = view.findViewById(R.id.radioGroupGender)
        edAge = view.findViewById(R.id.edAge)
        edHeight = view.findViewById(R.id.edHeight)
        edWeight = view.findViewById(R.id.edWeight)
        btnSave = view.findViewById(R.id.btnSave)
        btnDate = view.findViewById(R.id.btnDate)
        btnGoal = view.findViewById(R.id.btnGoal)
        lvRecords = view.findViewById(R.id.lvRecords)
        tvDate = view.findViewById(R.id.tvDate)



        // 初始化 SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sharedPreferencesUserData = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        // 設定ListView的Adapter
        recordsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recordsList)
        lvRecords.adapter = recordsAdapter

        // 設定 RadioGroup 的事件監聽
        radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.radioMale -> "男"
                else -> "女"
            }
        }

        // 日期選擇按鈕點擊事件
        btnDate.setOnClickListener {
            DatePicker()
        }

        // 保存成紀錄按鈕點擊事件
        btnSave.setOnClickListener {
            saveRecord()
        }

        // 保存成目標按鈕點擊事件
        btnGoal.setOnClickListener {
            saveGoal()
            //按下按鈕更新FragmentTwo的畫面
            (activity as? MainActivity)?.adapter?.updateFragmentTwo()
        }

        // 設置點擊 ListView 列表項目進行刪除
        lvRecords.setOnItemClickListener { _, _, position, _ ->
            showConfirmDialog(position)
        }

        // 載入已儲存的資料
        loadSavedRecords()



        // 返回 View 對象
        return view
    }

    //保存目標
   /* private fun saveGoal() {
        val age = edAge.text.toString()
        val height = edHeight.text.toString()
        val weight = edWeight.text.toString()

        if (age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty()) {
            val editor = sharedPreferencesUserData.edit()

            // 保存目標資料
            editor.putString("weight", weight)
            editor.putString("gender", selectedGender)
            editor.putString("age", age)
            editor.putString("height", height)
            editor.apply()

            // 清空輸入框
            edAge.text.clear()
            edHeight.text.clear()
            edWeight.text.clear()
            tvDate.text = "請選擇日期"

            Toast.makeText(requireContext(), "個人資料設置完畢，去設置目標吧!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "資料填寫不完整", Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun saveGoal() {//儲存個人資料
        val age = edAge.text.toString()
        val height = edHeight.text.toString()
        val weight = edWeight.text.toString()

        if (age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty()) {
            val userData = UserData()

            // 更新 userData 物件
            userData.updateUserData(selectedGender, age.toInt(), height.toInt(), weight.toInt())

            // 使用 Gson 將 userData 存到 SharedPreferences
            val gson = Gson()
            val userDataJson = gson.toJson(userData)
            val editor = sharedPreferencesUserData.edit()
            editor.putString("userData", userDataJson)
            editor.apply()

            // 清空輸入框
            edAge.text.clear()
            edHeight.text.clear()
            edWeight.text.clear()
            tvDate.text = "請選擇日期"

            Toast.makeText(requireContext(), "個人資料設置完畢，去設置目標吧!", Toast.LENGTH_SHORT).show()

            // 通知 FragmentTwo 資料已更新
            (activity as? MainActivity)?.adapter?.updateFragmentTwo()
        } else {
            Toast.makeText(requireContext(), "資料填寫不完整", Toast.LENGTH_SHORT).show()
        }
    }


    // 顯示確認刪除對話框
    private fun showConfirmDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("刪除紀錄")
            .setMessage("確定要刪除這條紀錄嗎?")
            .setPositiveButton("確定"){_, _ ->
                //確定刪除資料
                deleteRecord(position)
            }
            .setNegativeButton("取消", null)
            .show()

    }

    private fun deleteRecord(position: Int) {
        recordsList.removeAt(position)
        recordsAdapter.notifyDataSetChanged()

        // 更新 SharedPreferences
        saveToSharedPreferences()
    }

    private fun DatePicker() {
        // 取得當前日期
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // 打開日期選擇器
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // 使用者選擇日期後的處理
            selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDay"
            tvDate.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
    }
    //儲存為紀錄-
    private fun saveRecord() {
        val age = edAge.text.toString()
        val height = edHeight.text.toString()
        val weight = edWeight.text.toString()
        val date = tvDate.text.toString()

        if (age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty() && tvDate.text != "請選擇日期") {
            // 將資料保存到列表中，並更新ListView
            val record = "日期: $date\t 年齡 : $age\n性別: $selectedGender\t 身高: $height cm\t 體重: $weight kg"
            recordsList.add(record)

            sortRecordByDate()

            recordsAdapter.notifyDataSetChanged()// 更新 ListView

            // 清空輸入框
            edAge.text.clear()
            edHeight.text.clear()
            edWeight.text.clear()
            tvDate.text = "請選擇日期"

            // 保存資料到 SharedPreferences
            saveToSharedPreferences()
            Toast.makeText(requireContext(), "紀錄已保存", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "資料填寫不完整", Toast.LENGTH_SHORT).show()
        }
    }

    //ListView按照日期大小排序
    private fun sortRecordByDate() {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        recordsList.sortByDescending { record ->
            // 提取紀錄中的日期部分並轉換為 Date 物件
            val dateString = record.substringAfter("日期: ").substringBefore("\t")
            dateFormat.parse(dateString) ?: Date(0) // 轉換失敗時使用日期 0（1970-01-01）
        }
    }

    // 將資料儲存到 SharedPreferences
    private fun saveToSharedPreferences() {
        val editor = sharedPreferences.edit()

        // 將 List 轉換為逗號分隔的字串
        val recordsString = recordsList.joinToString(",")
        editor.putString(RECORDS_KEY, recordsString)
        editor.apply()
    }

    // 從 SharedPreferences 載入儲存的資料
    private fun loadSavedRecords() {
        val savedRecords = sharedPreferences.getString(RECORDS_KEY, null)

        if (!savedRecords.isNullOrEmpty()) {
            // 如果資料存在，將其分割並加回到 recordsList
            recordsList.addAll(savedRecords.split(","))
            recordsAdapter.notifyDataSetChanged()
        }
    }


}