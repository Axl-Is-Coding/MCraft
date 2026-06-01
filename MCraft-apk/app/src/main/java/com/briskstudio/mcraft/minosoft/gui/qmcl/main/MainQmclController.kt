package de.bixilon.minosoft.gui.qmcl.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.bixilon.minosoft.gui.qmcl.controller.AndroidController

class MainQmclController : AndroidController() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qmcl_main)
        initialize()
    }
    
    override fun initialize() {
        // TODO: Initialize main UI components
    }
    
    override fun onBackPressedCustom(): Boolean {
        // Return true if handled
        return false
    }
    
    override fun onBackPressed() {
        if (!onBackPressedCustom()) {
            super.onBackPressed()
        }
    }
}
