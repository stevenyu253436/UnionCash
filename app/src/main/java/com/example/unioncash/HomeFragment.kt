package com.example.unioncash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.*
import com.example.unioncash.network.TronApi // 导入 TronApi

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var assetsAdapter: AssetsAdapter
    private lateinit var assetList: List<Asset>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAssets)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        assetList = mutableListOf(
            Asset("US Dollars", "USD", "0.00", R.drawable.ic_usd),
            Asset("Tether", "USDT", "0.000000", R.drawable.ic_usdt),
            Asset("Bitcoin", "BTC", "0.000000", R.drawable.ic_btc),
            Asset("Ethereum", "ETH", "0.000000", R.drawable.ic_eth)
        )

        assetsAdapter = AssetsAdapter(assetList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = assetsAdapter

        swipeRefreshLayout.setOnRefreshListener {
            refreshAssets()
        }

        return view
    }

    private fun refreshAssets() {
        // 示例地址和私钥
        val tronAddress = "TT8i1yRfNqGL7uudFNgruUFJpqchJjXYZF"

        CoroutineScope(Dispatchers.IO).launch {
            TronApi.getUsdtBalance(tronAddress) { balance ->
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("HomeFragment", "USDT Balance: $balance")
                    updateAssetList(balance)
                }
            }
        }
    }

    private suspend fun updateAssetList(balance: Double) {
        withContext(Dispatchers.Main) {
            val updatedList = assetList.toMutableList()
            updatedList[1] = Asset("Tether", "USDT", balance.toString(), R.drawable.ic_usdt) // 更新 USDT 余额
            assetList = updatedList
            assetsAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}