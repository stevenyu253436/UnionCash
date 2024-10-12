package com.example.unioncash

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class USDTTRCDetailActivity : AppCompatActivity() {

    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var usdtTRCBalance: TextView
    private lateinit var usdtEquivalent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usdt_trc_detail) // Updated layout for TRC

        // Initialize views
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        usdtTRCBalance = findViewById(R.id.tvUsdtBalance)
        usdtEquivalent = findViewById(R.id.tvUsdtEquivalent)

        // Set up the RecyclerView for the history
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)

        // Load USDT-TRC balance passed via Intent
        val balance = intent.getDoubleExtra("balance", 0.0)
        usdtTRCBalance.text = String.format("%.6f", balance)

        // Assume 1 USDT = 1 EUR for now
        val equivalent = balance // Sample conversion rate
        usdtEquivalent.text = String.format("≈＄%.2f", equivalent)

        // Set back button behavior
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            Log.d("USDTTRCDetailActivity", "Back button clicked") // Log the event
            finish()
        }

        // Load history data (You can implement history fetching from API)
        loadHistory()
    }

    private fun loadHistory() {
        // Load history data into RecyclerView (Implement fetching and displaying)
    }
}