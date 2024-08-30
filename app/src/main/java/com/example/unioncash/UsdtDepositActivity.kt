package com.example.unioncash

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UsdtDepositActivity : AppCompatActivity() {

    private val REQUEST_WRITE_STORAGE = 112

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usdt_deposit)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "充值"
        }

        // 找到 RadioGroup 和 RadioButtons
        val rgChainName = findViewById<RadioGroup>(R.id.rgChainName)
        val rbErc20 = findViewById<RadioButton>(R.id.rbErc20)
        val rbTrc20 = findViewById<RadioButton>(R.id.rbTrc20)

        // 设置 RadioGroup 的选中状态改变监听器
        rgChainName.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbErc20 -> {
                    rbErc20.setBackgroundResource(R.drawable.bg_selected_chain)
                    rbTrc20.setBackgroundResource(R.drawable.bg_unselected_chain)
                }
                R.id.rbTrc20 -> {
                    rbErc20.setBackgroundResource(R.drawable.bg_unselected_chain)
                    rbTrc20.setBackgroundResource(R.drawable.bg_selected_chain)
                }
            }
        }

        val qrCodeImageView: ImageView = findViewById(R.id.ivQrCode)
        val addressTextView: TextView = findViewById(R.id.tvAddress)
        val saveQrCodeButton: Button = findViewById(R.id.btnSaveQrCode)

        // 设置 USDT 地址
        val address = "TT8i1yRfNqGL7uudFNgruUFJpqchJjXYZF"
        addressTextView.text = address

        // 生成带图标的二维码
        val qrCodeBitmap = generateQRCodeWithLogo(address, R.drawable.ic_usdt)
        qrCodeImageView.setImageBitmap(qrCodeBitmap)

        // 请求写入存储权限
        val hasWriteStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE)
        }

        // 保存二维码图片的操作
        saveQrCodeButton.setOnClickListener {
            // 这里实现保存二维码的逻辑
            Toast.makeText(this, "二維碼圖片保存成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateQRCodeWithLogo(text: String, logoResId: Int): Bitmap? {
        val size = 800 // 二维码的尺寸
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)

        val qrCodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        // 加载图标
        val logoBitmap = BitmapFactory.decodeResource(resources, logoResId)
        val overlaySize = size / 5

        // 在二维码中心绘制图标
        val canvas = Canvas(qrCodeBitmap)
        val left = (qrCodeBitmap.width - overlaySize) / 2
        val top = (qrCodeBitmap.height - overlaySize) / 2
        canvas.drawBitmap(logoBitmap, null, Rect(left, top, left + overlaySize, top + overlaySize), null)

        return qrCodeBitmap
    }

    private fun saveQRCodeImage(qrCodeBitmap: Bitmap): Boolean {
        return try {
            val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/"
            val fileName = "usdt_qrcode.png"
            val file = File(filePath)
            if (!file.exists()) {
                file.mkdirs()
            }
            val imageFile = File(filePath, fileName)
            val outputStream = FileOutputStream(imageFile)
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
