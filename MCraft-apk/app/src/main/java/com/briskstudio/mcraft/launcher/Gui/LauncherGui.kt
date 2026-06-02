package com.briskstudio.mcraft.launcher.Gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.briskstudio.mcraft.R
import com.briskstudio.mcraft.launcher.Gui.Adapter.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class LauncherGUI : Fragment() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launcher_gui, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewPager = view.findViewById(R.id.view_pager)
        bottomNav = view.findViewById(R.id.bottom_navigation)
        
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
}