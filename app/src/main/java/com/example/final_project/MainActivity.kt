package com.example.final_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化 ViewPager2
        val viewPager2 = findViewById<ViewPager2>(R.id.viewPager2)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // 初始化 Adapter，這個 Adapter 管理 Fragment 的滑動
        adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter//viewPager2是一個adapter，負責管理fragment
        viewPager2.offscreenPageLimit = 2;

        //深度動畫效果
        viewPager2.setPageTransformer { page, position ->
            if (position <= 0) {
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            } else if (position <= 1) {
                page.alpha = 1 - position
                page.translationX = page.width * -position
                val scale = 0.75f + (1 - position) * 0.25f
                page.scaleX = scale
                page.scaleY = scale
            }
        }

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when(position){
                0 -> tab.text = "首頁"
                1 -> tab.text = "個人資料"
                2 -> tab.text = "運動"
                3 -> tab.text = "飲食"
            }
        }.attach()








    }
}