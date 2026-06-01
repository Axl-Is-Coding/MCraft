package de.bixilon.minosoft.gui.qmcl

import android.content.Intent
import de.bixilon.minosoft.android.MinosoftApplication
import de.bixilon.minosoft.gui.qmcl.main.MainQmclController
import de.bixilon.kutil.latch.SimpleLatch

object Qmcl {
    
    private val latch = SimpleLatch(2)  // Wait for 2 things to load
    
    var skipQmclStartup = false
    var initialized = false
        private set
    var visible: Boolean = false
        private set
    
    fun preload() {
        if (skipQmclStartup) return
        
        // Task 1: Load installed versions
        latch.dec()  // Signal that this task is done
        // TODO: Actually load versions from storage
        
        // Task 2: Load settings/preferences
        latch.dec()  // Signal that this task is done
        // TODO: Load SharedPreferences
        
        initialized = true
    }
    
    fun start() {
        if (!initialized) return
        if (latch.count >= 1) return  // Still loading, wait
        
        val context = MinosoftApplication.instance ?: return
        val intent = Intent(context, MainQmclController::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        visible = true
    }
    
    fun setVisibility(visible: Boolean) {
        if (visible == this.visible) return
        // TODO: Handle visibility changes if needed
        this.visible = visible
    }
}