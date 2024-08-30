package com.example.unioncash.network

import com.example.unioncash.model.WalletResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TronApi {
    @POST("BitoProServices/GetWallets")
    fun getWallets(@Body body: Map<String, String>): Call<WalletResponse>
}
