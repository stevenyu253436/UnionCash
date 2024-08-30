package com.example.unioncash

import android.content.Intent  // Import Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.unioncash.ui.theme.UnionCashTheme

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager  // Import LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_LANGUAGE_CHANGED = "com.example.unioncash.LANGUAGE_CHANGED"
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                loadFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_card -> {
                loadFragment(FragmentBoundCard())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                loadFragment(AccountFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.navigation)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    fun sendLanguageChangedBroadcast() {
        val intent = Intent(ACTION_LANGUAGE_CHANGED)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}