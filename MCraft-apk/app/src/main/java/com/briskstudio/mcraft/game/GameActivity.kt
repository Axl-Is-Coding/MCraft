package com.briskstudio.mcraft.game

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.briskstudio.mcraft.R

class GameActivity : AppCompatActivity() {
    
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: MCraftRenderer
    private lateinit var touchControls: TouchControlGUI
    
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
        
        // Create touch controls overlay
        touchControls = TouchControlGUI(this)
        touchControls.onMovement = { dx, dy ->
            // TODO: Send movement to game (WASD equivalent)
            // dx = left/right, dy = forward/back
        }
        
        touchControls.onLook = { deltaX, deltaY ->
            // TODO: Send camera look to game
        }
        
        touchControls.onJump = { pressed ->
            if (pressed) {
                // TODO: Send space key (jump)
            }
        }
        
        touchControls.onSneak = { pressed ->
            // TODO: Send shift key (sneak)
        }
        
        touchControls.onAttack = { pressed ->
            if (pressed) {
                // TODO: Send left click (attack)
            }
        }
        
        touchControls.onUse = { pressed ->
            if (pressed) {
                // TODO: Send right click (use)
            }
        }
        
        touchControls.onDrop = {
            // TODO: Send Q key (drop item)
        }
        
        touchControls.onInventory = {
            // TODO: Send E key (open inventory)
        }
        
        // Add touch controls as overlay
        addContentView(touchControls, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
    
    private fun setupMinosoft() {
        // Get AndroidRendering instance
        val androidRendering = AndroidRendering(playSession)
        androidRendering.setAndroidWindow(androidWindow)
        
        // Start the renderer
        val latch = ParentLatch(1)
        androidRendering.start(latch)
    }
}