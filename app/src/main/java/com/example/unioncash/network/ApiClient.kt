package com.example.unioncash.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    private const val BASE_URL = "https://gnugcc.ddns.net/api/"

    // 配置 OkHttpClient 忽略 SSL 证书验证
    private val okHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(getUnsafeSslSocketFactory(), object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        })
        .hostnameVerifier { _, _ -> true }
        .build()

    // 使用自定义的 OkHttpClient 来构建 Retrofit 实例
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)  // 使用自定义的 OkHttpClient
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tronApi: TronApi = retrofit.create(TronApi::class.java)

    // 定义不安全的 SSL Socket Factory（忽略 SSL 证书验证）
    private fun getUnsafeSslSocketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        return sslContext.socketFactory
    }
}
