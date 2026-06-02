package com.briskstudio.mcraft.game.Touch

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.briskstudio.mcraft.R

class TouchControlGUI(context: Context) : FrameLayout(context) {
    
    // Joystick variables
    private var joystickX = 0f
    private var joystickY = 0f
    private var joystickCenterX = 0f
    private var joystickCenterY = 0f
    private var joystickRadius = 80f
    private var isJoystickActive = false
    
    // Look area variables
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isLooking = false
    
    // Button states
    private var isJumpPressed = false
    private var isSneakPressed = false
    private var isAttackPressed = false
    private var isUsePressed = false
    private var isDropPressed = false
    private var isInventoryPressed = false
    
    // Callbacks for game input
    var onMovement: ((dx: Float, dy: Float) -> Unit)? = null
    var onLook: ((dx: Float, dy: Float) -> Unit)? = null
    var onJump: ((pressed: Boolean) -> Unit)? = null
    var onSneak: ((pressed: Boolean) -> Unit)? = null
    var onAttack: ((pressed: Boolean) -> Unit)? = null
    var onUse: ((pressed: Boolean) -> Unit)? = null
    var onDrop: (() -> Unit)? = null
    var onInventory: (() -> Unit)? = null
    
    init {
        setBackgroundColor(Color.TRANSPARENT)
        setupButtons()
    }
    
    private fun setupButtons() {
        // Create buttons programmatically for better control
        val buttonSize = dipToPx(60f)
        
        // Jump button (bottom right area)
        addTouchButton(buttonSize, "JUMP") { pressed ->
            isJumpPressed = pressed
            onJump?.invoke(pressed)
        }
        
        // Sneak button (bottom center)
        addTouchButton(buttonSize, "SNEAK") { pressed ->
            isSneakPressed = pressed
            onSneak?.invoke(pressed)
        }
        
        // Attack button (bottom right)
        addTouchButton(buttonSize, "ATTACK") { pressed ->
            isAttackPressed = pressed
            onAttack?.invoke(pressed)
        }
        
        // Use/Interact button (bottom right area)
        addTouchButton(buttonSize, "USE") { pressed ->
            isUsePressed = pressed
            onUse?.invoke(pressed)
        }
        
        // Drop button (bottom right)
        addTouchButton(buttonSize, "DROP") { pressed ->
            if (pressed) onDrop?.invoke()
        }
        
        // Inventory button (bottom right)
        addTouchButton(buttonSize, "INV") { pressed ->
            if (pressed) onInventory?.invoke()
        }
    }
    
    private fun addTouchButton(size: Int, text: String, onPress: (Boolean) -> Unit) {
        val button = object : View(context) {
            private val paint = Paint()
            private val textPaint = Paint()
            
            init {
                paint.color = Color.argb(180, 30, 30, 30)
                paint.isAntiAlias = true
                textPaint.color = Color.WHITE
                textPaint.textSize = 24f
                textPaint.isAntiAlias = true
                textPaint.textAlign = Paint.Align.CENTER
            }
            
            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                val cx = width / 2f
                val cy = height / 2f
                val radius = width / 2f
                canvas.drawCircle(cx, cy, radius, paint)
                canvas.drawText(text, cx, cy + 10, textPaint)
            }
            
            override fun onTouchEvent(event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onPress(true)
                        invalidate()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        onPress(false)
                        invalidate()
                    }
                }
                return true
            }
        }
        
        val params = LayoutParams(size, size)
        button.layoutParams = params
        addView(button)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val x = event.getX(0)
                val y = event.getY(0)
                
                // Left half = joystick, Right half = look area
                if (x < screenWidth / 2) {
                    startJoystick(x, y)
                } else {
                    startLook(x, y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isJoystickActive) {
                    updateJoystick(event.getX(0), event.getY(0))
                }
                if (isLooking) {
                    updateLook(event.getX(0), event.getY(0))
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (isJoystickActive) {
                    stopJoystick()
                }
                if (isLooking) {
                    stopLook()
                }
            }
        }
        return true
    }
    
    private fun startJoystick(x: Float, y: Float) {
        isJoystickActive = true
        joystickCenterX = x
        joystickCenterY = y
        joystickX = 0f
        joystickY = 0f
    }
    
    private fun updateJoystick(x: Float, y: Float) {
        var dx = (x - joystickCenterX) / joystickRadius
        var dy = (y - joystickCenterY) / joystickRadius
        
        // Clamp to circle
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        if (distance > 1f) {
            dx /= distance
            dy /= distance
        }
        
        joystickX = dx
        joystickY = -dy  // Invert Y for game coordinates
        
        onMovement?.invoke(joystickX, joystickY)
        invalidate()
    }
    
    private fun stopJoystick() {
        isJoystickActive = false
        joystickX = 0f
        joystickY = 0f
        onMovement?.invoke(0f, 0f)
        invalidate()
    }
    
    private fun startLook(x: Float, y: Float) {
        isLooking = true
        lastTouchX = x
        lastTouchY = y
    }
    
    private fun updateLook(x: Float, y: Float) {
        val deltaX = (x - lastTouchX) * 0.5f
        val deltaY = (y - lastTouchY) * 0.5f
        lastTouchX = x
        lastTouchY = y
        
        onLook?.invoke(deltaX, deltaY)
    }
    
    private fun stopLook() {
        isLooking = false
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw joystick if active
        if (isJoystickActive) {
            val paint = Paint()
            paint.color = Color.argb(100, 255, 255, 255)
            paint.isAntiAlias = true
            
            // Draw joystick base
            canvas.drawCircle(joystickCenterX, joystickCenterY, joystickRadius, paint)
            
            // Draw joystick thumb
            paint.color = Color.argb(200, 200, 200, 200)
            val thumbX = joystickCenterX + joystickX * joystickRadius
            val thumbY = joystickCenterY - joystickY * joystickRadius
            canvas.drawCircle(thumbX, thumbY, joystickRadius * 0.4f, paint)
        }
    }
    
    private fun dipToPx(dip: Float): Int {
        return (dip * context.resources.displayMetrics.density).toInt()
    }
}