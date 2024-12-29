package com.example.final_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fragmentTwo: FragmentTwo? = null
    private var fragmentThree: FragmentThree? = null
    private var fragmentFour: FragmentFour? = null

    override fun getItemCount(): Int {
        // 返回 ViewPager2 中頁面的數量
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        // 根據 position 返回對應的 Fragment
        return when (position) {
            0 -> FragmentOne() // 第一個頁面
            1 -> {
                if (fragmentTwo == null) {
                    fragmentTwo = FragmentTwo()
                }
                fragmentTwo!!
            } // 第二個頁面
            2 -> {
                if (fragmentThree == null) {
                    fragmentThree = FragmentThree()
                }
                fragmentThree!!
            } // 第三個頁面
            3 -> {
                if (fragmentFour == null) {
                    fragmentFour = FragmentFour()
                }
                fragmentFour!!
            }// 第四個頁面
            else -> FragmentFive()// 第五個頁面
        }
    }

    // 调用 FragmentTwo 的 loadData 方法
    fun updateFragmentTwo() {
        fragmentTwo?.loadData()
    }


    fun updateFragmentThree() {
        fragmentThree?.loadData()
    }

    fun updateFragmentFour() {
        fragmentFour?.loadData()
    }
}