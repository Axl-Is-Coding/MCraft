package com.briskstudio.mcraft.game.Render

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.briskstudio.mcraft.game.Render.System.opengles.OpenGLESRenderSystem
import com.briskstudio.mcraft.game.Render.System.window.AndroidWindow
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderSystem(
    private val versionId: String,
    private val context: Context
) : GLSurfaceView.Renderer {
    
    private lateinit var androidWindow: AndroidWindow
    private lateinit var glRenderSystem: OpenGLESRenderSystem
    private var isInitialized = false
    private var frameCount = 0
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set clear color to dark gray (will be replaced by game rendering)
        GLES30.glClearColor(0.1f, 0.1f, 0.15f, 1.0f)
        
        // Initialize Android window
        androidWindow = AndroidWindow(context)
        androidWindow.setSize(800, 600) // Will be updated in onSurfaceChanged
        
        // Initialize GLES render system
        glRenderSystem = OpenGLESRenderSystem()
        glRenderSystem.init()
        
        isInitialized = true
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        androidWindow.setSize(width, height)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        // Clear screen
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        
        renderloop.renderFrame()
    }
    
    fun onResume() {
        glRenderSystem.onResume()
    }
    
    fun onPause() {
        glRenderSystem.onPause()
    }
    
    fun onDestroy() {
        glRenderSystem.destroy()
        isInitialized = false
    }
    
    fun getAndroidWindow(): AndroidWindow = androidWindow
    fun getGLRenderSystem(): OpenGLESRenderSystem = glRenderSystem
}