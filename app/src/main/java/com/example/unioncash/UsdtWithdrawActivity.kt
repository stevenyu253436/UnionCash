package com.example.unioncash

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class UsdtWithdrawActivity : AppCompatActivity() {

    private val SCAN_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usdt_withdraw)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "提现"
        }

        // 获取布局中的视图
        val withdrawAmountEditText = findViewById<EditText>(R.id.etWithdrawAmount)
        val withdrawTypeSpinner = findViewById<Spinner>(R.id.spWithdrawType)
        val chainNameSpinner = findViewById<Spinner>(R.id.spChainName)
        val withdrawAddressEditText = findViewById<EditText>(R.id.etWithdrawAddress)
        val recipientEditText = findViewById<EditText>(R.id.etRecipient)
        val withdrawButton = findViewById<Button>(R.id.btnWithdraw)

        // 设置提现类型的选项
        val withdrawTypes = arrayOf("鏈上轉帳", "其他方式")
        withdrawTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, withdrawTypes)

        // 设置链名称的选项
        val chainNames = arrayOf("ETH/ERC20", "Tron/TRC20")
        chainNameSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chainNames)

        // 缩放 ic_usdt 图标
        val usdtIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_usdt)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size_small)
        var scaledBitmap = Bitmap.createScaledBitmap(usdtIcon, iconSize, iconSize, true)
        var drawable = BitmapDrawable(resources, scaledBitmap)
        withdrawAmountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

        // 缩放 ic_copy 图标
        val scanIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_scan)
        scaledBitmap = Bitmap.createScaledBitmap(scanIcon, iconSize, iconSize, true)
        drawable = BitmapDrawable(resources, scaledBitmap)
        withdrawAddressEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

        // 设置 EditText 的触摸监听，检测用户是否点击了 drawableEnd
        withdrawAddressEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (withdrawAddressEditText.right - withdrawAddressEditText.compoundDrawables[2].bounds.width())) {
                    // 用户点击了 drawableEnd 部分
                    val intent = Intent(this, QrCodeScannerActivity::class.java)
                    startActivityForResult(intent, SCAN_REQUEST_CODE)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // 提现按钮点击事件处理
        withdrawButton.setOnClickListener {
            val amount = withdrawAmountEditText.text.toString()
            val withdrawType = withdrawTypeSpinner.selectedItem.toString()
            val chainName = chainNameSpinner.selectedItem.toString()
            val address = withdrawAddressEditText.text.toString()
            val recipient = recipientEditText.text.toString()

            if (amount.isEmpty() || address.isEmpty() || recipient.isEmpty()) {
                Toast.makeText(this, "請填寫所有必填項", Toast.LENGTH_SHORT).show()
            } else {
                // 处理提现逻辑
                Toast.makeText(this, "提現成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
