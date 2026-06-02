package com.briskstudio.mcraft.game.Touch

import android.view.MotionEvent
import android.view.View

class TouchInputMapper(
    private val view: View,
    private val renderSystem: com.briskstudio.mcraft.game.Render.RenderSystem
) {
    
    companion object {
        private const val JOYSTICK_AREA = 0.25f  // Left 25% of screen
        private const val LOOK_AREA = 0.75f       // Right 75% of screen
        private const val DEADZONE = 0.1f
    }
    
    private var joystickX = 0f
    private var joystickY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isMoving = false
    private var isLooking = false
    
    fun handleTouchEvent(event: MotionEvent): Boolean {
        val screenWidth = view.width.toFloat()
        val screenHeight = view.height.toFloat()
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                handleTouchDown(event, screenWidth)
            }
            MotionEvent.ACTION_MOVE -> {
                handleTouchMove(event, screenWidth, screenHeight)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                handleTouchUp(event)
            }
        }
        return true
    }
    
    private fun handleTouchDown(event: MotionEvent, screenWidth: Float) {
        val x = event.getX(0) / screenWidth
        
        if (x < JOYSTICK_AREA) {
            // Joystick area - movement
            isMoving = true
            joystickX = (event.getX(0) / screenWidth - 0.125f) * 8f
            joystickY = (event.getY(0) / view.height - 0.5f) * 2f
            
            // Clamp to deadzone
            if (Math.abs(joystickX) < DEADZONE) joystickX = 0f
            if (Math.abs(joystickY) < DEADZONE) joystickY = 0f
            
            // TODO: Send movement input to game
            // renderSystem.onMovement(joystickX, -joystickY)
        } else {
            // Look area - camera
            isLooking = true
            lastTouchX = event.getX(0)
            lastTouchY = event.getY(0)
        }
    }
    
    private fun handleTouchMove(event: MotionEvent, screenWidth: Float, screenHeight: Float) {
        val x = event.getX(0) / screenWidth
        
        if (x < JOYSTICK_AREA && isMoving) {
            // Update joystick position
            joystickX = (event.getX(0) / screenWidth - 0.125f) * 8f
            joystickY = (event.getY(0) / view.height - 0.5f) * 2f
            
            if (Math.abs(joystickX) < DEADZONE) joystickX = 0f
            if (Math.abs(joystickY) < DEADZONE) joystickY = 0f
            
            // TODO: Update movement
        } else if (isLooking) {
            // Update camera look
            val deltaX = (event.getX(0) - lastTouchX) / screenWidth * 100f
            val deltaY = (event.getY(0) - lastTouchY) / screenHeight * 100f
            lastTouchX = event.getX(0)
            lastTouchY = event.getY(0)
            
            // TODO: Send camera rotation to game
            // renderSystem.onLook(deltaX, deltaY)
        }
    }
    
    private fun handleTouchUp(event: MotionEvent) {
        // Reset movement when touch released
        isMoving = false
        isLooking = false
        joystickX = 0f
        joystickY = 0f
        
        // TODO: Stop movement input
        // renderSystem.onMovement(0f, 0f)
    }
    
    fun getJoystickX(): Float = joystickX
    fun getJoystickY(): Float = joystickY
    fun isMoving(): Boolean = isMoving
}