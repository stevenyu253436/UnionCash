package com.example.unioncash

import java.util.Locale
import android.content.Intent // 导入 Intent 类
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

// 导入 ApiClient
import com.example.unioncash.network.ApiClient

// 添加以下三行
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

        // 找到你的按钮
        val rechargeButton: Button = view.findViewById(R.id.btnRecharge)

        // 设置点击事件监听器
        rechargeButton.setOnClickListener {
            // 点击按钮后启动 RechargeActivity
            val intent = Intent(requireContext(), RechargeActivity::class.java)
            startActivity(intent)
        }

        // 找到你的提現按钮
        val withdrawButton: Button = view.findViewById(R.id.btnWithdraw)
        withdrawButton.setOnClickListener {
            val intent = Intent(requireContext(), WithdrawActivity::class.java)
            startActivity(intent)
        }
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
}