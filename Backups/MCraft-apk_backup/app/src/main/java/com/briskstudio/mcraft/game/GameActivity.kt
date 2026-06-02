package com.briskstudio.mcraft.game

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.briskstudio.mcraft.R

class GameActivity : AppCompatActivity() {
    
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: MCraftRenderer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get version from intent
        val versionId = intent.getStringExtra("VERSION_ID") ?: "1.20.4"
        val versionName = intent.getStringExtra("VERSION_NAME") ?: "Minecraft 1.20.4"
        title = versionName
        
        // Setup OpenGL ES 3.0 surface
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(3)  // OpenGL ES 3.0
        
        // Create renderer
        renderer = MCraftRenderer(versionId)
        glSurfaceView.setRenderer(renderer)
        
        // Set render mode
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        
        setContentView(glSurfaceView)
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}