package com.example.unioncash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap  // 导入 Bitmap
import android.graphics.BitmapFactory  // 导入 BitmapFactory
import android.graphics.drawable.BitmapDrawable  // 导入 BitmapDrawable
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.google.zxing.ResultPoint

class QrCodeScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView

    // 定義相機請求代碼
    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val REQUEST_CODE_PICK_IMAGE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        // 檢查相機權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 沒有權限，請求權限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            // 有權限，初始化掃碼
            setupBarcodeScanner()
        }

        // Set up the custom toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size_small) // 例如，24dp

        drawable?.let {
            val scaledBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_back),
                iconSize, iconSize, true
            )
            btnBack.setImageDrawable(BitmapDrawable(resources, scaledBitmap))
        }

        // Set back button action
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Set album button action
        val tvAlbum: TextView = findViewById(R.id.tvAlbum)
        tvAlbum.setOnClickListener {
            // Open the album or gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        barcodeView = findViewById(R.id.zxing_barcode_scanner)
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val intent = Intent()
                    intent.putExtra("SCAN_RESULT", it.text)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
    }

    // 初始化掃碼器
    private fun setupBarcodeScanner() {
        barcodeView = findViewById(R.id.zxing_barcode_scanner)
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val intent = Intent()
                    intent.putExtra("SCAN_RESULT", it.text)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
    }

    // 處理權限請求結果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 獲得權限，初始化掃碼
                setupBarcodeScanner()
            } else {
                // 權限被拒絕，顯示錯誤提示
                Toast.makeText(this, "相機權限被拒絕", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    // 添加 onActivityResult 方法
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            // 这里你可以使用 selectedImageUri 处理选中的图片
        }
    }
}
