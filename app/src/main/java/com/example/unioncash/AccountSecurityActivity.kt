package com.example.unioncash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Locale

class AccountSecurityActivity : AppCompatActivity() {

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
