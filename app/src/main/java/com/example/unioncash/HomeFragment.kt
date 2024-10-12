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
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.pow

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
            Asset("US Dollars", "USD", "0.000000", R.drawable.ic_usd, "0.00"), // 对应的USD值
            Asset("Tether (TRC20)", "USDT-TRC20", "0.000000", R.drawable.ic_usdt, "0.00"), // USDT-TRC20
            Asset("Tether (ERC20)", "USDT-ERC20", "0.000000", R.drawable.ic_usdt, "0.00") // USDT-ERC20，默认余额为 0
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

        exchangeButton.setOnClickListener {
            val intent = Intent(requireContext(), ExchangeActivity::class.java)
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
        val tronAddress = "TAMGw3VQMj9RUDQXAMPYVE7TqrckpqHrrS"
        val requestBody = mapOf("address" to tronAddress)

        // 假設這裡獲取到 authToken
        val authToken = "ZXlKaGJHY2lPaUpCTWpVMlEwSkRMVWhUTlRFeUlpd2lkSGx3SWpvaVNsZFVJbjAuZXlKMWMyVnlJam9pZEdWemRIVnpaWElpTENKMWJtbHhkV1ZmYm1GdFpTSTZJblJsYzNSdVlXMWxJaXdpYm1KbUlqb3hOekkyTVRBMk1qVTFMQ0psZUhBaU9qRTNNall4TURZek1UVXNJbWxoZENJNk1UY3lOakV3TmpJMU5Td2lhWE56SWpvaVIyVjBWRzlyWlc0aWZRLk9vWFRyOFY4d2xaN2VldGlrM2VFVlhvbElvNUtkbnhyZFBrTURueXRnVEtDTmRCWnk1bXRKYk5jd0ZvX25nRmUwUXhWc0Z1MlI5ZzdoSXB6dWxja1NB"

        val etherscanApiKey = "H6WZH2NCZVQCQUNQJIKAH9TRFCINEKHNI5"
        val erc20ContractAddress = "0xdAC17F958D2ee523a2206206994597C13D831ec7" // USDT Contract Address
        val walletAddress = "0x1E7B10AaF9888a6b1FED08E72859351d465c5932"
        val etherscanUrl = "https://api.etherscan.io/api?module=account&action=tokenbalance&contractaddress=$erc20ContractAddress&address=$walletAddress&tag=latest&apikey=$etherscanApiKey"

        CoroutineScope(Dispatchers.IO).launch {
            var trc20Balance = 0.0
            var erc20Balance = 0.0

            // 同時查詢TRC20 和 ERC20 的餘額
            val trc20Job = launch {
                try {
                    // 使用帶有 AuthToken 的 Retrofit 實例
                    val retrofit = ApiClient.getRetrofitInstance(authToken)
                    val tronApi = retrofit.create(TronApi::class.java)

                    // 1. 查询 Tron 的 USDT 余额
                    val tronCall = tronApi.getWallets(requestBody)
                    val tronResponse = tronCall.execute()

                    if (tronResponse.isSuccessful) {
                        val walletResponse = tronResponse.body()
                        trc20Balance = walletResponse?.data?.responseWallets?.find { it.coinType == "USDT" }?.amount ?: 0.0
                    } else {
                        Log.e("HomeFragment", "Tron API error: ${tronResponse.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Exception in TRC20: ${e.message}")
                }
            }

            val erc20Job = launch {
                try {
                    // 2. 查询 ERC-20 USDT 余额
                    val client = OkHttpClient()
                    val request = Request.Builder().url(etherscanUrl).build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val balanceInWei = jsonResponse.getString("result").toBigInteger()

                        // USDT has 6 decimals, so divide by 10^6 to get the actual balance
                        erc20Balance = balanceInWei.toDouble() / 10.0.pow(6)
                    } else {
                        Log.e("HomeFragment", "Etherscan API error: ${response.message}")
                    }
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Exception in ERC20: ${e.message}")
                }
            }

            // 等待兩個協程完成
            trc20Job.join()
            erc20Job.join()

            // 3. 调用 `updateAssetList` 更新 TRC20 和 ERC20 的资产信息
            withContext(Dispatchers.Main) {
                updateAssetList(trc20Balance, erc20Balance)
            }
        }
    }

    private suspend fun updateAssetList(trc20Balance: Double, erc20Balance: Double) {
        withContext(Dispatchers.Main) {
            val updatedList = assetList.toMutableList()

            // 更新 TRC20 资产
            val trc20UsdValue = String.format(Locale.US, "%.2f", trc20Balance)
            val formattedTrc20Balance = String.format(Locale.US, "%.6f", trc20Balance)

            val usdtTrc20Index = updatedList.indexOfFirst { it.symbol == "USDT-TRC20" }
            if (usdtTrc20Index != -1) {
                Log.d("UpdateAssetList", "Updating USDT to $formattedTrc20Balance with USD value $trc20UsdValue")
                updatedList[usdtTrc20Index] = Asset(
                    name = "Tether (TRC20)",
                    symbol = "USDT-TRC20",
                    amount = formattedTrc20Balance,
                    iconResId = R.drawable.ic_usdt,
                    usdValue = trc20UsdValue
                )
            }

            // 更新 ERC20 资产
            val erc20UsdValue = String.format(Locale.US, "%.2f", erc20Balance)
            val formattedErc20Balance = String.format(Locale.US, "%.6f", erc20Balance)

            val usdtErc20Index = updatedList.indexOfFirst { it.symbol == "USDT-ERC20" }
            if (usdtErc20Index != -1) {
                Log.d("UpdateAssetList", "Updating USDT-ERC20 to $formattedErc20Balance with USD value $erc20UsdValue")
                updatedList[usdtErc20Index] = Asset(
                    name = "Tether (ERC20)",
                    symbol = "USDT-ERC20",
                    amount = formattedErc20Balance,
                    iconResId = R.drawable.ic_usdt,
                    usdValue = erc20UsdValue
                )

                // Handle click event for USDT-ERC20
                recyclerView.findViewHolderForAdapterPosition(usdtErc20Index)?.itemView?.setOnClickListener {
                    val intent = Intent(requireContext(), USDTERCDetailActivity::class.java)
                    intent.putExtra("balance", erc20Balance) // Pass balance to detail screen
                    startActivity(intent)
                }
            }

            // 使用 setData 方法更新数据并刷新 RecyclerView
            assetsAdapter.setData(updatedList)
            Log.d("HomeFragment", "Updated assetList: $updatedList")

            // 计算 TRC20 和 ERC20 的总和
            val totalUSDT = trc20Balance + erc20Balance
            val formattedTotalUSDT = String.format(Locale.US, "%.2f", totalUSDT)

            // 更新总资产 TextView，显示总 USDT 余额
            tvTotalAmount.text = "$formattedTotalUSDT USDT"
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