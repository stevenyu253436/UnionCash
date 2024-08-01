package com.example.unioncash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userNameTextView: TextView = view.findViewById(R.id.tvUserName)
        val userInitialTextView: TextView = view.findViewById(R.id.tvUserInitial)

        // 假设用户名是从某个地方获取到的
        val userName = "Chewei Yu"

        // 提取姓氏和名字的首字母
        val initials = getInitials(userName)

        // 设置首字母到 TextView
        userInitialTextView.text = initials

        // 设置完整用户名到 TextView
        userNameTextView.text = userName
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) {
            // 获取姓氏和名字的首字母
            parts[0].firstOrNull()?.toUpperCase().toString() + parts[1].firstOrNull()?.toUpperCase().toString()
        } else {
            // 如果名字只有一个单词，只提取第一个字母
            parts[0].firstOrNull()?.toUpperCase().toString()
        }
    }
}