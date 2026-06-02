package de.bixilon.minosoft.gui.rendering.system

import de.bixilon.kmath.vec.vec2.i.Vec2i
import de.bixilon.minosoft.data.text.formatting.color.RGBAColor
import de.bixilon.minosoft.gui.rendering.system.base.*
import de.bixilon.minosoft.gui.rendering.system.base.buffer.frame.Framebuffer
import de.bixilon.minosoft.gui.rendering.system.base.buffer.frame.attachment.depth.DepthModes
import de.bixilon.minosoft.gui.rendering.system.base.buffer.frame.attachment.stencil.StencilModes
import de.bixilon.minosoft.gui.rendering.system.base.buffer.frame.attachment.texture.TextureModes
import de.bixilon.minosoft.gui.rendering.system.base.buffer.uniform.FloatUniformBuffer
import de.bixilon.minosoft.gui.rendering.system.base.buffer.vertex.PrimitiveTypes
import de.bixilon.minosoft.gui.rendering.system.base.buffer.vertex.VertexBuffer
import de.bixilon.minosoft.gui.rendering.system.base.query.QueryTypes
import de.bixilon.minosoft.gui.rendering.system.base.query.RenderQuery
import de.bixilon.minosoft.gui.rendering.system.base.shader.ShaderManagement
import de.bixilon.minosoft.gui.rendering.system.base.texture.TextureManager
import de.bixilon.minosoft.gui.rendering.system.base.texture.data.buffer.TextureBuffer
import de.bixilon.minosoft.gui.rendering.util.mesh.struct.MeshStruct
import com.briskstudio.mcraft.game.Render.System.opengles.OpenGLESRenderSystem
import java.nio.FloatBuffer
import java.nio.IntBuffer

class MinosoftRenderSystem : RenderSystem {
    
    private val backend = OpenGLESRenderSystem.getInstance()
    
    // Required properties
    override lateinit var shader: ShaderManagement
    override lateinit var vendor: GPUVendor
    override var framebuffer: Framebuffer? = null
    override val primitives: Set<PrimitiveTypes> = setOf(PrimitiveTypes.TRIANGLE)
    override val active: Boolean get() = backend.isInitialized()
    
    // Optional properties
    override lateinit var vendorString: String
    override lateinit var version: String
    override lateinit var gpuType: String
    
    // State tracking
    private var currentClearColor = RGBAColor(0f, 0f, 0f, 1f)
    private var currentViewport = Vec2i(0, 0)
    private var currentDepthFunc = DepthFunctions.LESS
    private var currentDepthMask = true
    
    override fun init() {
        vendorString = "MCraft Android Renderer"
        version = "OpenGL ES 3.0"
        gpuType = "Android GPU"
        vendor = GPUVendor.UNKNOWN
        backend.init()
    }
    
    override fun destroy() {
        backend.destroy()
    }
    
    override fun enable(capability: RenderingCapabilities) = set(capability, true)
    override fun disable(capability: RenderingCapabilities) = set(capability, false)
    
    override fun set(capability: RenderingCapabilities, status: Boolean) {
        when (capability) {
            RenderingCapabilities.DEPTH_TEST -> backend.enableDepthTest(status)
            RenderingCapabilities.BLENDING -> backend.enableBlending(status)
            RenderingCapabilities.FACE_CULLING -> backend.enableCulling(status)
            RenderingCapabilities.POLYGON_OFFSET -> { /* Not critical for V1 */ }
            else -> {}
        }
    }
    
    override fun get(capability: RenderingCapabilities): Boolean {
        return when (capability) {
            RenderingCapabilities.DEPTH_TEST -> backend.isDepthTestEnabled()
            RenderingCapabilities.BLENDING -> backend.isBlendingEnabled()
            RenderingCapabilities.FACE_CULLING -> backend.isCullingEnabled()
            else -> false
        }
    }
    
    override fun set(source: BlendingFunctions, destination: BlendingFunctions) {
        backend.setBlendFunc(source, destination)
    }
    
    override fun setBlendFunction(
        sourceRGB: BlendingFunctions,
        destinationRGB: BlendingFunctions,
        sourceAlpha: BlendingFunctions,
        destinationAlpha: BlendingFunctions
    ) {
        backend.setBlendFuncSeparate(sourceRGB, destinationRGB, sourceAlpha, destinationAlpha)
    }
    
    override var depth: DepthFunctions
        get() = currentDepthFunc
        set(value) {
            currentDepthFunc = value
            backend.setDepthFunc(value)
        }
    
    override var depthMask: Boolean
        get() = currentDepthMask
        set(value) {
            currentDepthMask = value
            backend.setDepthMask(value)
        }
    
    override var polygonMode: PolygonModes
        get() = PolygonModes.FILL
        set(value) { /* GLES doesn't support polygon mode */ }
    
    override var clearColor: RGBAColor
        get() = currentClearColor
        set(value) {
            currentClearColor = value
            backend.setClearColor(value)
        }
    
    override var viewport: Vec2i
        get() = currentViewport
        set(value) {
            currentViewport = value
            backend.setViewport(value.x, value.y)
        }
    
    override fun readPixels(start: Vec2i, size: Vec2i): TextureBuffer {
        return backend.readPixels(start, size)
    }
    
    override fun createVertexBuffer(
        struct: MeshStruct,
        data: FloatBuffer,
        primitive: PrimitiveTypes,
        index: IntBuffer?,
        reused: Boolean
    ): VertexBuffer {
        return backend.createVertexBuffer(struct, data, primitive, index, reused)
    }
    
    override fun createFloatUniformBuffer(data: FloatBuffer): FloatUniformBuffer {
        return backend.createFloatUniformBuffer(data)
    }
    
    override fun createFramebuffer(
        size: Vec2i,
        scale: Float,
        texture: TextureModes?,
        depth: DepthModes?,
        stencil: StencilModes?
    ): Framebuffer {
        return backend.createFramebuffer(size, scale, texture, depth, stencil)
    }
    
    override fun createQuery(type: QueryTypes): RenderQuery {
        return backend.createQuery(type)
    }
    
    override fun createTextureManager(): TextureManager {
        return backend.createTextureManager()
    }
    
    override fun clear(vararg buffers: IntegratedBufferTypes) {
        backend.clearBuffers(*buffers)
    }
    
    override fun getErrors(): List<RenderSystemError> {
        return emptyList()
    }
    
    override fun polygonOffset(factor: Float, unit: Float) {
        // GLES supports this
        backend.polygonOffset(factor, unit)
    }
    
    override fun resetBlending() {
        disable(RenderingCapabilities.BLENDING)
        setBlendFunction(BlendingFunctions.ONE, BlendingFunctions.ONE_MINUS_SOURCE_ALPHA, BlendingFunctions.ONE, BlendingFunctions.ZERO)
    }
}