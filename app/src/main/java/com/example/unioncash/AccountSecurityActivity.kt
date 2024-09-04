package com.example.unioncash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AccountSecurityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_security)

        // 啟用返回按鈕
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 處理返回按鈕點擊事件
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()  // 或者直接使用 finish() 結束當前 Activity
        return true
    }
}