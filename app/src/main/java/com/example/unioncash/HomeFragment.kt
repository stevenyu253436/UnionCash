package com.example.unioncash

import java.util.Locale
import android.content.Context
import android.content.Intent // 导入 Intent 类
import android.content.res.Configuration // 添加此导入
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView // 确保导入 TextView 类
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.*
import com.example.unioncash.network.TronApi // 导入 TronApi
import com.example.unioncash.network.ApiClient
import com.example.unioncash.model.WalletResponse
import com.example.unioncash.model.Data
import com.example.unioncash.model.ResponseWallet

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var assetsAdapter: AssetsAdapter
    private lateinit var assetList: List<Asset>
    private lateinit var tvTotalAmount: TextView // 声明 TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAssets)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        // 初始化 tvTotalAmount
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)

        assetList = mutableListOf(
            Asset("US Dollars", "USD", "0.00", R.drawable.ic_usd, "0.00"), // 对应的USD值
            Asset("Tether", "USDT", "0.000000", R.drawable.ic_usdt, "0.00"), // 对应的USD值
        )

        assetsAdapter = AssetsAdapter(assetList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = assetsAdapter

        swipeRefreshLayout.setOnRefreshListener {
            refreshAssets()
        }

        // 在 onCreateView 方法中直接调用 refreshAssets
        refreshAssets()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 应用语言设置
        applyLanguageSettings()

        // 找到你的按钮
        val rechargeButton: Button = view.findViewById(R.id.btnRecharge)
        val withdrawButton: Button = view.findViewById(R.id.btnWithdraw)
        val exchangeButton: Button = view.findViewById(R.id.btnExchange)
        val globalSpeedButton: Button = view.findViewById(R.id.btnGlobalSpeed)

        // 设置点击事件监听器
        rechargeButton.setOnClickListener {
            // 点击按钮后启动 RechargeActivity
            val intent = Intent(requireContext(), RechargeActivity::class.java)
            startActivity(intent)
        }

        // 找到你的提現按钮
        withdrawButton.setOnClickListener {
            val intent = Intent(requireContext(), WithdrawActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // 重新应用语言设置
        applyLanguageSettings()
    }

    private fun applyLanguageSettings() {
        // 从 SharedPreferences 中获取用户的语言偏好
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val preferredLanguage = sharedPreferences.getString("My_Lang", "繁體中文")
        setAppLanguage(requireContext(), preferredLanguage!!)

        // 更新按钮的文本
        val rechargeButton: Button = view?.findViewById(R.id.btnRecharge) ?: return
        val withdrawButton: Button = view?.findViewById(R.id.btnWithdraw) ?: return
        val exchangeButton: Button = view?.findViewById(R.id.btnExchange) ?: return
        val globalSpeedButton: Button = view?.findViewById(R.id.btnGlobalSpeed) ?: return

        rechargeButton.text = getString(R.string.recharge)
        withdrawButton.text = getString(R.string.withdraw)
        exchangeButton.text = getString(R.string.exchange)
        globalSpeedButton.text = getString(R.string.global_speed_transfer)

        // 其他需要更新的视图文本
        val homeLabel: TextView = view?.findViewById(R.id.tvHomeLabel) ?: return
        homeLabel.text = getString(R.string.title_home)

        val totalAssetsLabel: TextView = view?.findViewById(R.id.tvTotalAssets) ?: return
        totalAssetsLabel.text = getString(R.string.total_assets)
    }

    private fun refreshAssets() {
        val tronAddress = "TT8i1yRfNqGL7uudFNgruUFJpqchJjXYZF"
        val requestBody = mapOf("address" to tronAddress)

        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.tronApi.getWallets(requestBody)
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val walletResponse = response.body()
                    val usdtBalance = walletResponse?.data?.responseWallets?.find { it.coinType == "USDT" }?.amount ?: 0.0

                    withContext(Dispatchers.Main) {
                        Log.d("HomeFragment", "USDT Balance: $usdtBalance")
                        updateAssetList(usdtBalance)
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Exception: ${e.message}")
            }
        }
    }

    private suspend fun updateAssetList(balance: Double) {
        withContext(Dispatchers.Main) {
            val updatedList = assetList.toMutableList()

            // 假设 USDT 对 USD 的汇率为 1:1
            val usdValue = String.format(Locale.US, "%.2f", balance)
            val formattedBalance = String.format(Locale.US, "%.2f", balance)

            val usdtAssetIndex = updatedList.indexOfFirst { it.symbol == "USDT" }
            if (usdtAssetIndex != -1) {
                Log.d("UpdateAssetList", "Updating USDT to $formattedBalance with USD value $usdValue")
                updatedList[usdtAssetIndex] = Asset(
                    name = "Tether",
                    symbol = "USDT",
                    amount = formattedBalance,
                    iconResId = R.drawable.ic_usdt,
                    usdValue = usdValue
                )
            }

            // 使用 setData 方法更新数据并刷新 RecyclerView
            assetsAdapter.setData(updatedList)
            Log.d("HomeFragment", "Updated assetList: $updatedList")

            // 更新总资产 TextView
            Log.d("UpdateAssetList", "Setting tvTotalAmount to: $formattedBalance USDT")
            tvTotalAmount.text = "$formattedBalance USDT"
            swipeRefreshLayout.isRefreshing = false
        }
    }

    // 设置语言的函数
    private fun setAppLanguage(context: Context, language: String) {
        // 打印日志，查看传入的语言
        Log.d("HomeFragment", "Setting app language to: $language")

        val locale = when (language) {
            "繁體中文" -> Locale("zh", "TW")
            "English" -> Locale.ENGLISH
            else -> Locale("zh", "TW") // 默认为繁体中文
        }

        // 打印设置的Locale
        Log.d("HomeFragment", "Locale being set to: ${locale.language}_${locale.country}")

        // 设置默认的Locale
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // 确保创建新的配置上下文，并打印新的Locale
        val newContext = context.createConfigurationContext(config)
        Log.d("HomeFragment", "New Configuration applied with Locale: ${newContext.resources.configuration.locales[0].language}_${newContext.resources.configuration.locales[0].country}")

        // 更新当前的配置，使用此配置更新资源
        val resources = context.resources
        resources.updateConfiguration(config, resources.displayMetrics)

        Log.d("HomeFragment", "New Configuration applied with Locale: ${resources.configuration.locales[0].language}_${resources.configuration.locales[0].country}")

        // 记录语言设置是否被成功保存
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("My_Lang", language)
            apply()
        }
        Log.d("HomeFragment", "Language saved to preferences: $language")

        // 调用时查看具体的设置是否被应用
        updateTextViewLanguage(newContext)
    }

    private fun updateTextViewLanguage(context: Context) {
        // 更新 HomeFragment 上的文本为新的语言
        val homeLabel: TextView = view?.findViewById(R.id.tvHomeLabel) ?: return
        homeLabel.text = context.getString(R.string.title_home)
        Log.d("HomeFragment", "Home label updated to: ${homeLabel.text}")

        val totalAssetsLabel: TextView = view?.findViewById(R.id.tvTotalAssets) ?: return
        totalAssetsLabel.text = context.getString(R.string.total_assets)
        Log.d("HomeFragment", "Total assets label updated to: ${totalAssetsLabel.text}")
    }
}