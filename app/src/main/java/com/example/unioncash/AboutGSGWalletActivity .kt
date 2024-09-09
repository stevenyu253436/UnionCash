package com.example.unioncash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutGSGWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_gsg_wallet)

        // 启用返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
