package com.briskstudio.mcraft.game.Render.System.window

import android.content.Context
import android.view.SurfaceView

class AndroidWindow(private var context: Context?) {
    
    private var width: Int = 0
    private var height: Int = 0
    private var windowTitle: String = "MCraft"
    private var isFullscreen: Boolean = false
    private var surfaceView: SurfaceView? = null
    
    fun setContext(context: Context) {
        this.context = context
    }
    
    fun setSurfaceView(view: SurfaceView) {
        surfaceView = view
    }
    
    fun getSurfaceView(): SurfaceView? = surfaceView
    
    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
        surfaceView?.layoutParams = android.view.ViewGroup.LayoutParams(width, height)
    }
    
    fun getWidth(): Int = width
    fun getHeight(): Int = height
    
    fun setTitle(title: String) {
        windowTitle = title
        (context as? android.app.Activity)?.title = title
    }
    
    fun setFullscreen(fullscreen: Boolean) {
        isFullscreen = fullscreen
        val activity = context as? android.app.Activity
        if (fullscreen) {
            activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
            activity?.window?.decorView?.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
        } else {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
    
    fun swapBuffers() {
        // GLSurfaceView handles this automatically
    }
    
    fun show() {
        surfaceView?.visibility = android.view.View.VISIBLE
    }
    
    fun hide() {
        surfaceView?.visibility = android.view.View.GONE
    }
    
    companion object {
    private var instance: AndroidWindow? = null
    
    fun getInstance(): AndroidWindow {
        if (instance == null) {
            instance = AndroidWindow(null)
        }
        return instance!!
    }
    
    fun setInstance(window: AndroidWindow) {
        instance = window
    }
}
    
}