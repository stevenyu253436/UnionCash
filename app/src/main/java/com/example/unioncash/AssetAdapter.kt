package com.example.unioncash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AssetsAdapter(private val assetList: List<Asset>) :
    RecyclerView.Adapter<AssetsAdapter.AssetViewHolder>() {

    class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewCurrencyName: TextView = itemView.findViewById(R.id.textViewCurrencyName)
        val textViewCurrencyCode: TextView = itemView.findViewById(R.id.textViewCurrencyCode)
        val textViewAssetAmount: TextView = itemView.findViewById(R.id.textViewAssetAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset, parent, false)
        return AssetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assetList[position]
        holder.imageViewIcon.setImageResource(asset.iconResId)
        holder.textViewCurrencyName.text = asset.name
        holder.textViewCurrencyCode.text = asset.code
        holder.textViewAssetAmount.text = asset.amount
    }

    override fun getItemCount() = assetList.size
}