package com.example.unioncash.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object TronApi {
    private const val BASE_URL = "https://api.trongrid.io"
    private const val USDT_CONTRACT_ADDRESS = "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf" // USDT 合约地址

    private val client = OkHttpClient()

    fun getUsdtBalance(address: String, callback: (Double) -> Unit) {
        val url = "$BASE_URL/v1/accounts/$address/assets"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(0.0)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(0.0)
                        return
                    }

                    val jsonResponse = JSONObject(it.body!!.string())
                    Log.d("TronApi", "API Response: $jsonResponse") // 添加这一行

                    val dataArray = jsonResponse.getJSONArray("data")
                    var balance = 0.0

                    for (i in 0 until dataArray.length()) {
                        val asset = dataArray.getJSONObject(i)
                        if (asset.getString("key") == USDT_CONTRACT_ADDRESS) {
                            balance = asset.getDouble("value") / 1e6
                            break
                        }
                    }
                    callback(balance)
                }
            }
        })
    }
}
