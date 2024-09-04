package com.example.unioncash

import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast // Add this import
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Locale
import com.example.unioncash.AccountSecurityFragment
import androidx.fragment.app.FragmentManager // Add this import
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope

class AccountFragment : Fragment() {

    private val languageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MainActivity.ACTION_LANGUAGE_CHANGED) {
                Log.d("AccountFragment", "Received language change broadcast")
                updateUIWithNewContext(requireContext())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AccountFragment", "onViewCreated called")

        val userNameTextView: TextView = view.findViewById(R.id.tvUserName)
        val userInitialTextView: TextView = view.findViewById(R.id.tvUserInitial)
        val languageSetting: View = view.findViewById(R.id.language_setting) // 初始化 languageSetting

        // 假设用户名是从某个地方获取到的
        val userName = "Chewei Yu"

        // 提取姓氏和名字的首字母
        val initials = getInitials(userName)

        // 设置首字母到 TextView
        userInitialTextView.text = initials

        // 设置完整用户名到 TextView
        userNameTextView.text = userName

        // 載入偏好的語言，如果沒有則預設為繁體中文
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val preferredLanguage = sharedPreferences.getString("My_Lang", "繁體中文")
        Log.d("AccountFragment", "Preferred language retrieved: $preferredLanguage")
        setAppLanguage(requireContext(), preferredLanguage!!) // 注意這裡傳入 context

        // 綁定語言設定的點擊事件
        languageSetting.setOnClickListener {
            showLanguageSelectionDialog()
        }

        // 使用 Intent 來啟動 AccountSecurityActivity
        val accountSecurityLayout: View = view.findViewById(R.id.accountSecurityLayout)
        accountSecurityLayout.setOnClickListener {
            val intent = Intent(requireContext(), AccountSecurityActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) {
            // 获取姓氏和名字的首字母
            parts[0].firstOrNull()?.toUpperCase().toString() + parts[1].firstOrNull()?.toUpperCase().toString()
        } else {
            // 如果名字只有一个单词，只提取第一个字母
            parts[0].firstOrNull()?.toUpperCase().toString()
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("繁體中文", "English")
        val checkedItem = getSelectedLanguageIndex() // 獲取當前語言的索引

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("語言")
            .setSingleChoiceItems(languages, checkedItem, null) // 不做即时更新
            .setPositiveButton("確認") { dialog, _ ->
                val selectedLanguage = languages[(dialog as AlertDialog).listView.checkedItemPosition]
                Log.d("AccountFragment", "Language selected: $selectedLanguage")
                setAppLanguage(requireContext(), selectedLanguage) // 只有在確認時才更新語言
                restartApp()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun getSelectedLanguageIndex(): Int {
        val currentLanguage = Locale.getDefault().language
        Log.d("AccountFragment", "Current language: $currentLanguage")
        return when (currentLanguage) {
            "zh" -> 0 // 繁體中文
            "en" -> 1 // English
            else -> 0
        }
    }

    fun setAppLanguage(context: Context, language: String) {
        val locale = when (language) {
            "繁體中文" -> Locale("zh", "TW")
            "English" -> Locale.ENGLISH
            else -> Locale("zh", "TW")
        }

        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
//        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        // 使用 createConfigurationContext 创建一个新的上下文
        val newContext = context.createConfigurationContext(config)

        Log.d("AccountFragment", "Setting app language to: $language (${locale.language}_${locale.country})")
        Log.d("AccountFragment", "Locale set to: ${locale.displayName} (${locale.language}_${locale.country})")

        // 保存選擇的語言到 SharedPreferences
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("My_Lang", language)
            apply()
        }

        // 發送語言更改廣播通知
        sendLanguageChangedBroadcast(context)

        // 更新应用的语言设置，并将新上下文应用于界面元素
        updateUIWithNewContext(newContext)
    }

    private fun sendLanguageChangedBroadcast(context: Context) {
        val intent = Intent(MainActivity.ACTION_LANGUAGE_CHANGED)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        Log.d("AccountFragment", "Language change broadcast sent")
    }

    private fun updateUIWithNewContext(context: Context) {
        // 更新 Fragment 中的所有 TextView 文本
        Log.d("AccountFragment", "Updating UI with new context")

        // 確保重新獲取視圖引用
        val view = this.view ?: return

        val userNameTextView: TextView = view?.findViewById(R.id.tvUserName) ?: return
        val userInitialTextView: TextView = view?.findViewById(R.id.tvUserInitial) ?: return
        val accountTitleTextView: TextView = view?.findViewById(R.id.tvAccountTitle) ?: return
        val accountSecurityTextView: TextView = view?.findViewById(R.id.tvAccountSecurity) ?: return // 新增這行
        val identityVerificationTextView: TextView = view?.findViewById(R.id.tvIdentityVerification) ?: return // 新增這行
        val accountManagementTextView: TextView = view?.findViewById(R.id.tvAccountManagement) ?: return // 新增這行
        val addressBookTextView: TextView = view?.findViewById(R.id.tvAddressBook) ?: return // 新增這行
        val serviceFeeTextView: TextView = view?.findViewById(R.id.tvServiceFee) ?: return // 新增這行
        val languageSettingsTextView: TextView = view?.findViewById(R.id.tvLanguageSettings) ?: return // 新增這行
        val helpTextView: TextView = view?.findViewById(R.id.tvHelp) ?: return // 新增這行
        val aboutGSGWalletTextView: TextView = view?.findViewById(R.id.tvAboutGSGWallet) ?: return // 新增這行
        val logoutTextView: TextView = view?.findViewById(R.id.btnLogout) ?: return // 新增這行

        // 使用新上下文更新字符串资源
        accountTitleTextView.text = context.getString(R.string.account)
        accountSecurityTextView.text = context.getString(R.string.account_security) // 更新帳戶 & 安全的文本
        identityVerificationTextView.text = context.getString(R.string.identity_verification) // 更新身份認證的文本
        accountManagementTextView.text = context.getString(R.string.account_management) // 更新賬戶管理的文本
        addressBookTextView.text = context.getString(R.string.address_book) // 更新地址簿的文本
        serviceFeeTextView.text = context.getString(R.string.service_fees) // 更新服務費用的文本
        languageSettingsTextView.text = context.getString(R.string.language_settings) // 更新語言設定的文本
        helpTextView.text = context.getString(R.string.help) // 更新幫助的文本
        aboutGSGWalletTextView.text = context.getString(R.string.about_gsg_wallet) // 更新關於GSG Wallet的文本
        logoutTextView.text = context.getString(R.string.logout) // 更新Logout的文本

        // 重新綁定導航的點擊事件
        view.findViewById<View>(R.id.accountSecurityLayout)?.setOnClickListener {
            if (findNavController().currentDestination?.id != R.id.accountSecurityFragment) {
                findNavController().navigate(R.id.action_accountFragment_to_accountSecurityFragment)
            }
        }
    }

    private fun restartApp() {
        Log.d("AccountFragment", "Restarting app")
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }
}