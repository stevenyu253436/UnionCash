package com.example.unioncash.adapter

import android.content.Intent
import android.util.Log // 导入 Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unioncash.R
import com.example.unioncash.RechargeDetailActivity
import com.example.unioncash.UsdtDepositActivity
import com.example.unioncash.UsdtWithdrawActivity // 导入 UsdtWithdrawActivity
import com.example.unioncash.model.Currency

class CurrencyAdapter(
    private val currencies: List<Currency>,
    private val isWithdrawal: Boolean // 添加 isWithdrawal 标志
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.ivIcon)
        val name: TextView = itemView.findViewById(R.id.tvName)
        val symbol: TextView = itemView.findViewById(R.id.tvSymbol)

        fun bind(currency: Currency) {
            // Bind data to the views
            icon.setImageResource(currency.iconResId)
            name.text = currency.name
            symbol.text = currency.symbol

            // Set an onClickListener to the entire itemView
            itemView.setOnClickListener {
                // Create an intent to start RechargeDetailActivity
                val context = itemView.context
                val intent = when {
                    currency.symbol == "USDT" && isWithdrawal -> {
                        // 如果是 USDT 且是提现操作，启动 UsdtWithdrawActivity
                        Log.d("CurrencyAdapter", "Navigating to UsdtWithdrawActivity")
                        Intent(context, UsdtWithdrawActivity::class.java)
                    }
                    currency.symbol == "USDT" -> {
                        // 如果是 USDT 且是充值操作，启动 UsdtDepositActivity
                        Log.d("CurrencyAdapter", "Navigating to UsdtDepositActivity")
                        Intent(context, UsdtDepositActivity::class.java)
                    }
                    else -> {
                        // 对其他货币的处理逻辑
                        Log.d("CurrencyAdapter", "Navigating to RechargeDetailActivity")
                        Intent(context, RechargeDetailActivity::class.java)
                    }
                }

                // Pass the currency details to the RechargeDetailActivity
                intent.putExtra("CURRENCY_NAME", currency.name)
                intent.putExtra("CURRENCY_SYMBOL", currency.symbol)
                intent.putExtra("CURRENCY_ICON", currency.iconResId)

                // Start the new activity
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencies[position]
        holder.bind(currency)
    }

    override fun getItemCount(): Int = currencies.size
}
