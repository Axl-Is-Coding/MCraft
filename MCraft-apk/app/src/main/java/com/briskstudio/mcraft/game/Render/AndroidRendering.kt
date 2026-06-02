package com.briskstudio.mcraft.game.Render

import de.bixilon.kutil.latch.AbstractLatch
import de.bixilon.kutil.latch.ParentLatch
import de.bixilon.minosoft.protocol.network.session.play.PlaySession
import de.bixilon.minosoft.util.logging.Log
import de.bixilon.minosoft.util.logging.LogLevels
import de.bixilon.minosoft.util.logging.LogMessageType
import com.briskstudio.mcraft.game.Render.System.window.AndroidWindow
import com.briskstudio.mcraft.game.Render.System.opengles.OpenGLESRenderSystem

class AndroidRendering(private val session: PlaySession) {
    
    companion object {
        private var glRenderSystem: OpenGLESRenderSystem? = null
        private var androidWindow: AndroidWindow? = null
    }
    
    fun start(latch: AbstractLatch) {
        Log.log(LogMessageType.RENDERING, LogLevels.INFO) { "MCraft: Initializing Android renderer" }
        
        val loadingLatch = ParentLatch(1, latch)
        initializeRenderer(loadingLatch)
        latch.dec()
    }
    
    private fun initializeRenderer(latch: AbstractLatch) {
        try {
            // Initialize Android window (will be set by GameActivity)
            androidWindow = AndroidWindow(null) // Context will be set later
            
            // Initialize OpenGL ES render system
            glRenderSystem = OpenGLESRenderSystem()
            glRenderSystem?.init()
            
            Log.log(LogMessageType.RENDERING, LogLevels.INFO) { "MCraft: OpenGL ES renderer initialized" }
            
            // TODO: Connect to Minosoft's RenderContext
            // This will be done when GameActivity provides the SurfaceView
            
            latch.dec()
        } catch (e: Exception) {
            Log.log(LogMessageType.RENDERING, LogLevels.FATAL) { "Failed to initialize Android renderer: ${e.message}" }
            e.printStackTrace()
            latch.dec()
        }
    }
    
    fun setAndroidWindow(window: AndroidWindow) {
        androidWindow = window
        Log.log(LogMessageType.RENDERING, LogLevels.VERBOSE) { "Android window set" }
    }
    
    fun getGLRenderSystem(): OpenGLESRenderSystem? = glRenderSystem
    
    fun onResume() {
        glRenderSystem?.let {
            Log.log(LogMessageType.RENDERING, LogLevels.VERBOSE) { "Renderer resumed" }
        }
    }
    
    fun onPause() {
        glRenderSystem?.let {
            Log.log(LogMessageType.RENDERING, LogLevels.VERBOSE) { "Renderer paused" }
        }
    }
    
    fun onDestroy() {
        glRenderSystem?.destroy()
        glRenderSystem = null
        androidWindow = null
        Log.log(LogMessageType.RENDERING, LogLevels.INFO) { "Android renderer destroyed" }
    }
}