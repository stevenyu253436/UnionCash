package com.example.unioncash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Locale

class AccountSecurityActivity : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val languageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MainActivity.ACTION_LANGUAGE_CHANGED) {
                // 重新加载语言设置并更新UI
                applyLanguageSetting()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyLanguageSetting() // 直接应用语言设置
        setContentView(R.layout.activity_account_security)

        // 启用返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 更新标题activity_identity_verification
        updateTitleText()

        // 注册语言变更广播接收器
        LocalBroadcastManager.getInstance(this).registerReceiver(
            languageChangeReceiver, IntentFilter(MainActivity.ACTION_LANGUAGE_CHANGED)
        )

        // 初始化面容/指纹登录
        setupBiometricPrompt()

        // 设置面容/指纹登录开关点击事件
        val biometricSwitch: Switch = findViewById(R.id.biometricSwitch)
        biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                authenticateBiometric()
            }
        }
    }

    // 初始化 BiometricPrompt
    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                // 在这里执行认证成功后的操作
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("生物識别登錄")
            .setSubtitle("請使用您的面容或指紋解鎖")
            .setNegativeButtonText("取消")
            .build()
    }

    // 启动生物识别认证
    private fun authenticateBiometric() {
        // 检查设备是否支持生物识别
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // 设备支持生物识别，启动认证
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "设备没有生物识别硬件", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "生物识别硬件当前不可用", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "设备上没有录入的生物识别凭证", Toast.LENGTH_SHORT).show()
                // 提示用户设置生物识别凭证
                val enrollIntent = Intent(android.provider.Settings.ACTION_BIOMETRIC_ENROLL)
                enrollIntent.putExtra(android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG)
                startActivityForResult(enrollIntent, 1001)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次恢复时确保UI更新
        applyLanguageSetting()
        updateTitleText()
    }

    // 更新标题文本
    private fun updateTitleText() {
        val tvAccountSecurityTitle: TextView = findViewById(R.id.tvAccountSecurityTitle)
        tvAccountSecurityTitle.text = getString(R.string.account_security)
        Log.d("AccountSecurityActivity", "Title updated to: ${tvAccountSecurityTitle.text}")
    }

    // 处理返回按钮点击事件
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // 应用语言设置
    private fun applyLanguageSetting() {
        val language = getSavedLanguage() ?: "zh" // 默认繁体中文
        val locale = getLocaleForLanguage(language)
        setLocale(locale)
        saveLanguageToPreferences(language)
        Log.d("AccountSecurityActivity", "Applied language: $language, Locale: ${locale.language}_${locale.country}")
    }

    // 获取保存的语言设置
    private fun getSavedLanguage(): String? {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("My_Lang", null)
    }

    // 根据语言获取Locale
    private fun getLocaleForLanguage(language: String): Locale {
        return when (language) {
            "繁體中文" -> Locale("zh", "TW")
            "English" -> Locale.ENGLISH
            else -> Locale("zh", "TW")
        }
    }

    // 设置Locale
    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    // 保存语言到SharedPreferences
    private fun saveLanguageToPreferences(language: String) {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("My_Lang", language)
            apply()
        }
        Log.d("AccountSecurityActivity", "Language saved to preferences: $language")
    }
}
