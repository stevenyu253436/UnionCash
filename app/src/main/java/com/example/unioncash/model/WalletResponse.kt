package com.example.unioncash.model

data class WalletResponse(
    val data: Data
)

data class Data(
    val responseWallets: List<ResponseWallet>
)

data class ResponseWallet(
    val coinType: String,
    val amount: Double
)
