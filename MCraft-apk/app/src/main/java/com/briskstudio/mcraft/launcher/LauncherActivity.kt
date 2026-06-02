package com.briskstudio.mcraft.launcher

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.briskstudio.mcraft.R
import com.briskstudio.mcraft.launcher.Backend.Launcher
import com.briskstudio.mcraft.launcher.Gui.Adapter.ViewPagerAdapter
import com.briskstudio.mcraft.launcher.Gui.Fragment.AboutUI
import com.briskstudio.mcraft.launcher.Gui.Fragment.HomeUI
import com.briskstudio.mcraft.launcher.Gui.Fragment.SettingsUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class LauncherActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        
        viewPager = findViewById(R.id.view_pager)
        bottomNav = findViewById(R.id.bottom_navigation)
        
        setupViewPager()
        setupBottomNav()
    }
    
    private fun setupViewPager() {
        val fragments = listOf(
            HomeUI(),
            AboutUI(),
            SettingsUI()
        )
        
        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false  // Disable swipe, use bottom nav only
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNav.selectedItemId = R.id.nav_home
                    1 -> bottomNav.selectedItemId = R.id.nav_about
                    2 -> bottomNav.selectedItemId = R.id.nav_settings
                }
            }
        })
    }
    
    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.nav_about -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.nav_settings -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }
    
    // Optional: Handle back button to exit app when on Home screen
    private var backPressedTime: Long = 0
    
    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
                return
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        } else {
            // Go to Home tab instead of exiting
            viewPager.currentItem = 0
        }
    }
}