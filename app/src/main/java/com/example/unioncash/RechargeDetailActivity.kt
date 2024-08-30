package com.example.unioncash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView

class RechargeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge_detail)

        val currencyName = intent.getStringExtra("CURRENCY_NAME")
        val currencySymbol = intent.getStringExtra("CURRENCY_SYMBOL")
        val currencyIconResId = intent.getIntExtra("CURRENCY_ICON", 0)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = currencyName
        }

        val iconView: ImageView = findViewById(R.id.currencyIcon)
        val nameView: TextView = findViewById(R.id.currencyName)
        val symbolView: TextView = findViewById(R.id.currencySymbol)

        iconView.setImageResource(currencyIconResId)
        nameView.text = currencyName
        symbolView.text = currencySymbol

        // Set up additional details like QR code, address, etc.
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
