package com.example.unioncash

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UsdtExchangeActivity : AppCompatActivity() {

    // UI 元素
    private lateinit var spinnerPaymentAccount: Spinner
    private lateinit var tvPaySymbol: TextView
    private lateinit var tvReceiveSymbol: TextView
    private lateinit var etExchangeAmount: EditText
    private lateinit var tvAll: TextView
    private lateinit var tvExchangeableAmount: TextView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usdt_exchange)  // 確保這個佈局文件存在

        // 初始化 UI 元素
        spinnerPaymentAccount = findViewById(R.id.spinnerPaymentAccount)
        tvPaySymbol = findViewById(R.id.tvPaySymbol)
        tvReceiveSymbol = findViewById(R.id.tvReceiveSymbol)
        etExchangeAmount = findViewById(R.id.etExchangeAmount)
        tvAll = findViewById(R.id.tvAll)
        tvExchangeableAmount = findViewById(R.id.tvExchangeableAmount)
        btnConfirm = findViewById(R.id.btnConfirm)

        // 設置返回按鈕
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "兌換"
        }

        // 模擬數據：付款帳戶選擇器選項
        val accountOptions = arrayOf("USD賬戶")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentAccount.adapter = adapter

        // 添加事件監聽
        setEventListeners()
    }

    private fun setEventListeners() {
        // 當點擊 "全部" 按鈕時，設置 EditText 的值為全部可兌換的金額
        tvAll.setOnClickListener {
            etExchangeAmount.setText("100.00")  // 模擬數據，可以動態更新
        }

        // 當點擊 "確認" 按鈕時，進行兌換
        btnConfirm.setOnClickListener {
            val enteredAmount = etExchangeAmount.text.toString()
            if (enteredAmount.isNotEmpty()) {
                // 實現兌換邏輯（例如，調用API進行交易）
                Toast.makeText(this, "兌換 $enteredAmount USDT 成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "請輸入兌換金額", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
