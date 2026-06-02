package de.bixilon.minosoft.gui.rendering.window

import de.bixilon.kmath.vec.vec2.f.Vec2f
import de.bixilon.kmath.vec.vec2.i.Vec2i
import de.bixilon.minosoft.assets.AssetsManager
import de.bixilon.minosoft.config.profile.profiles.rendering.RenderingProfile
import de.bixilon.minosoft.gui.rendering.system.window.Window
import de.bixilon.minosoft.gui.rendering.system.window.CursorModes
import de.bixilon.minosoft.gui.rendering.system.window.CursorShapes
import de.bixilon.minosoft.gui.rendering.system.base.texture.data.buffer.TextureBuffer
import com.briskstudio.mcraft.game.Render.System.window.AndroidWindow

class MinosoftWindow : Window {
    
    private val backend = AndroidWindow.getInstance()
    
    override val systemScale: Vec2f
        get() = Vec2f(backend.getDensity(), backend.getDensity())
    
    override var size: Vec2i
        get() = Vec2i(backend.getWidth(), backend.getHeight())
        set(value) {
            backend.setSize(value.x, value.y)
        }
    
    override var minSize: Vec2i = Vec2i(300, 100)
    override var maxSize: Vec2i = Vec2i(-1, -1)
    
    override var visible: Boolean
        get() = backend.isVisible()
        set(value) = backend.setVisible(value)
    
    override var resizable: Boolean = false
    
    override var fullscreen: Boolean
        get() = backend.isFullscreen()
        set(value) = backend.setFullscreen(value)
    
    override var swapInterval: Int
        get() = backend.getSwapInterval()
        set(value) = backend.setSwapInterval(value)
    
    override var cursorMode: CursorModes = CursorModes.NORMAL
    override var cursorShape: CursorShapes = CursorShapes.ARROW
    
    override var title: String
        get() = backend.getTitle()
        set(value) = backend.setTitle(value)
    
    override val iconified: Boolean = false
    override val focused: Boolean = true
    
    override fun init(profile: RenderingProfile) {
        // Android window is already initialized by GameActivity
        // Just apply any settings from profile
        size = Vec2i(backend.getWidth(), backend.getHeight())
    }
    
    override fun begin() {
        backend.beginFrame()
    }
    
    override fun end() {
        backend.endFrame()
    }
    
    override fun destroy() {
        backend.destroy()
    }
    
    override fun close() {
        backend.close()
    }
    
    override fun forceClose() {
        backend.close()
    }
    
    override fun pollEvents() {
        backend.pollEvents()
    }
    
    override fun setIcon(buffer: TextureBuffer) {
        // Android icon is set in manifest
    }
    
    override fun setDefaultIcon(assetsManager: AssetsManager) {
        // Android icon is set in manifest
    }
    
    override fun resetCursor() {
        // No cursor on Android
    }
}