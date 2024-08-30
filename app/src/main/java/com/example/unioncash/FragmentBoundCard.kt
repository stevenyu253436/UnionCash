package com.example.unioncash

import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.ImageButton  // 添加此行
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.util.Locale

class FragmentBoundCard : Fragment() {

    private val languageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MainActivity.ACTION_LANGUAGE_CHANGED) {
                Log.d("FragmentBoundCard", "Received language change broadcast")
                updateUIWithNewContext(requireContext())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("FragmentBoundCard", "onCreateView called")
        return inflater.inflate(R.layout.fragment_bound_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FragmentBoundCard", "onViewCreated called")

        // 从 SharedPreferences 中读取语言设置，并应用
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val preferredLanguage = sharedPreferences.getString("My_Lang", "繁體中文")
        setAppLanguage(requireContext(), preferredLanguage!!)

        // 注册语言更改的广播接收器
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            languageChangeReceiver,
            IntentFilter(MainActivity.ACTION_LANGUAGE_CHANGED)
        )
        Log.d("FragmentBoundCard", "Broadcast receiver registered")
    }

    private fun updateUIWithNewContext(context: Context) {
        val boundCardTitleTextView: TextView = view?.findViewById(R.id.tvBoundCardTitle) ?: return
        boundCardTitleTextView.text = context.getString(R.string.card)

        val totalAssetsLabelTextView: TextView = view?.findViewById(R.id.tvTotalAssetsLabel) ?: return
        totalAssetsLabelTextView.text = context.getString(R.string.total_assets)

        val transferInButton: ImageButton = view?.findViewById(R.id.btnTransferIn) ?: return
        val transferInLabel: TextView = view?.findViewById(R.id.tvTransferInLabel) ?: return
        transferInLabel.text = context.getString(R.string.transfer_in)
        transferInButton.setImageResource(R.drawable.ic_transfer_in)

        // 添加 transferOutButton 和 transferOutLabel 的处理逻辑
        val transferOutButton: ImageButton = view?.findViewById(R.id.btnTransferOut) ?: return
        val transferOutLabel: TextView = view?.findViewById(R.id.tvTransferOutLabel) ?: return
        transferOutLabel.text = context.getString(R.string.transfer_out)
        transferOutButton.setImageResource(R.drawable.ic_transfer_out)

        // 添加 ViewPinButton 和 ViewPinLabel 的处理逻辑
        val viewPinButton: ImageButton = view?.findViewById(R.id.btnViewPin) ?: return
        val viewPinLabel: TextView = view?.findViewById(R.id.tvViewPinLabel) ?: return
        viewPinLabel.text = context.getString(R.string.view_pin)
        viewPinButton.setImageResource(R.drawable.ic_view_pin)

        // 添加 LockUnlockButton 和 LockUnlockLabel 的处理逻辑
        val lockUnlockButton: ImageButton = view?.findViewById(R.id.btnLockUnlock) ?: return
        val lockUnlockLabel: TextView = view?.findViewById(R.id.tvLockUnlockLabel) ?: return
        lockUnlockLabel.text = context.getString(R.string.lock_unlock)
        lockUnlockButton.setImageResource(R.drawable.ic_lock_unlock)
    }

    private fun setAppLanguage(context: Context, language: String) {
        val locale = when (language) {
            "繁體中文" -> Locale("zh", "TW")
            "English" -> Locale.ENGLISH
            else -> Locale("zh", "TW")
        }

        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        val newContext = context.createConfigurationContext(config)

        // 保存选择的语言到 SharedPreferences
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("My_Lang", language)
            apply()
        }

        // 更新应用的语言设置，并将新上下文应用于界面元素
        updateUIWithNewContext(newContext)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentBoundCard", "onDestroyView called, unregistering receiver")
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(languageChangeReceiver)
    }
}
