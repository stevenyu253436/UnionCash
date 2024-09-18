package com.example.unioncash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unioncash.adapter.CurrencyAdapter
import com.example.unioncash.model.Currency

class ExchangeActivity : AppCompatActivity() {

    // 定義 recyclerView 和 currencyAdapter 變量
    private lateinit var recyclerView: RecyclerView
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var currencyList: List<Currency>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "幣種清單"
        }

        recyclerView = findViewById(R.id.recyclerViewCurrencies)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 假设你已经有这些资源 ID 可用
        currencyList = listOf(
            Currency("US Dollars", "USD", R.drawable.ic_usd),
            Currency("Tether", "USDT", R.drawable.ic_usdt),
        )

        // 创建 CurrencyAdapter 时传递 isWithdrawal 参数，表示这是充值操作
        currencyAdapter = CurrencyAdapter(currencyList, isWithdrawal = false, isExchange = true)
        recyclerView.adapter = currencyAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
