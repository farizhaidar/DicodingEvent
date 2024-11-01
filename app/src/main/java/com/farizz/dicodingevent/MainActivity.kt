package com.farizz.dicodingevent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UpcomingFragment())
                .commit()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_upcoming -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, UpcomingFragment())
                        .commit()
                    true
                }
                R.id.nav_finished -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FinishedFragment())
                        .commit()
                    true
                }
                R.id.nav_Favorite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FavoriteFragment())
                        .commit()
                    true
                }
                R.id.nav_settings -> { // Tambahkan case ini
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
