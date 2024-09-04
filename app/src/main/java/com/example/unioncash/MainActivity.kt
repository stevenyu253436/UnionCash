package com.example.unioncash

import android.content.Context  // Import Context
import android.content.Intent  // Import Intent
import android.os.Bundle
import android.util.Log  // Import for logging
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
import androidx.navigation.findNavController  // Import NavController
import androidx.navigation.fragment.NavHostFragment  // Import NavHostFragment
import androidx.navigation.ui.setupWithNavController  // Import for BottomNavigationView
import com.example.unioncash.R
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_LANGUAGE_CHANGED = "com.example.unioncash.LANGUAGE_CHANGED"
        const val TAG = "MainActivity"
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
                // Use navigation component for AccountFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NavHostFragment.create(R.navigation.account_nav_graph))
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: Activity started")

        // Ensure language is applied before setting content view
        loadLanguageSetting()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.navigation)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        reloadBottomNavigationView()

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadLanguageSetting() {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val preferredLanguage = sharedPreferences.getString("My_Lang", "繁體中文")

        // Log the preferred language loaded
        Log.d(TAG, "loadLanguageSetting: Preferred language is $preferredLanguage")

        setAppLanguage(this, preferredLanguage!!)
    }

    fun setAppLanguage(context: Context, language: String) {
        Log.d(TAG, "setAppLanguage: Setting language to $language")

        val locale = when (language) {
            "繁體中文" -> Locale("zh", "TW")
            "English" -> Locale.ENGLISH
            else -> Locale("zh", "TW")
        }

        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)

        // Log the current locale set
        Log.d(TAG, "setAppLanguage: Locale set to ${locale.language}_${locale.country}")

        context.createConfigurationContext(config)

        // 通知應用語言已更改
        sendLanguageChangedBroadcast()

        // Log that language change broadcast was sent
        Log.d(TAG, "setAppLanguage: Language change broadcast sent")
    }

    private fun reloadBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.navigation)

        // Get the updated context with the new language configuration
        val newContext = this.createConfigurationContext(this.resources.configuration)

        // Log the current language configuration
        val currentLocale = newContext.resources.configuration.locales.get(0)
        Log.d(TAG, "Current Locale: ${currentLocale.language}_${currentLocale.country}")

        // Force the use of the new context when inflating the menu
        val inflater = menuInflater
        navView.menu.clear()

        // Re-inflate the menu using the updated context to ensure the strings reflect the new language
        navView.inflateMenu(R.menu.bottom_navigation)

        // Manually set the menu titles with localized strings
        navView.menu.findItem(R.id.navigation_home).title = newContext.getString(R.string.title_home)
        navView.menu.findItem(R.id.navigation_card).title = newContext.getString(R.string.title_card)
        navView.menu.findItem(R.id.navigation_account).title = newContext.getString(R.string.title_account)

        // Re-apply the navigation listener after inflating the menu
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Log the titles of the menu items to verify they are updated
        for (i in 0 until navView.menu.size()) {
            val item = navView.menu.getItem(i)
            Log.d(TAG, "Menu item: ${item.title}")
        }

        // Log that BottomNavigationView has been reloaded
        Log.d(TAG, "reloadBottomNavigationView: BottomNavigationView reloaded with new language")
    }

    fun sendLanguageChangedBroadcast() {
        val intent = Intent(ACTION_LANGUAGE_CHANGED)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}