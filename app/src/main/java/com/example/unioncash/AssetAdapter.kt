package com.example.unioncash

import android.util.Log // 导入 Log 类
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AssetsAdapter(private var assetList: List<Asset>) :
    RecyclerView.Adapter<AssetsAdapter.AssetViewHolder>() {

    class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewCurrencyName: TextView = itemView.findViewById(R.id.textViewCurrencyName)
        val textViewCurrencyCode: TextView = itemView.findViewById(R.id.textViewCurrencyCode)
        val textViewAssetAmount: TextView = itemView.findViewById(R.id.textViewAssetAmount)
        val textViewUsdValue: TextView = itemView.findViewById(R.id.textViewUsdValue) // 新增的 USD 值的 TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset, parent, false)
        return AssetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assetList[position]
        Log.d("AssetsAdapter", "Binding asset: ${asset.symbol} with value: ${asset.amount} and USD value: ${asset.usdValue}")
        holder.imageViewIcon.setImageResource(asset.iconResId)
        holder.textViewCurrencyName.text = asset.name
        holder.textViewCurrencyCode.text = asset.symbol // 显示符号
        holder.textViewAssetAmount.text = asset.amount
        // 确保显示 "≈$" 符号
        holder.textViewUsdValue.text = "≈\$${asset.usdValue}"
    }

    override fun getItemCount() = assetList.size

    // 新增的 setData 方法，用于更新 assetList 数据并刷新 RecyclerView
    fun setData(newAssetList: List<Asset>) {
        assetList = newAssetList
        notifyDataSetChanged()
    }
}