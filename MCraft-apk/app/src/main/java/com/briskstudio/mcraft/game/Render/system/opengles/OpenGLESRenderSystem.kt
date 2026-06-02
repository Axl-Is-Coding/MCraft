package com.briskstudio.mcraft.game.Render.System.opengles

import android.opengl.GLES30

class OpenGLESRenderSystem {
    
    companion object {
        private const val TAG = "OpenGLESRenderSystem"
    }
    
    companion object {
    private var instance: OpenGLESRenderSystem? = null
    
    fun getInstance(): OpenGLESRenderSystem {
        if (instance == null) {
            instance = OpenGLESRenderSystem()
        }
        return instance!!
    }
}
    
    private var isInitialized = false
    private var programId = -1
    private var vboId = -1
    
    fun init() {
        // Enable depth testing for 3D
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthFunc(GLES30.GL_LEQUAL)
        
        // Enable face culling (don't draw back faces)
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)
        
        // Enable blending for transparency
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        
        // Create a simple shader program for testing
        createSimpleShader()
        
        isInitialized = true
    }
    
    private fun createSimpleShader() {
        val vertexShaderCode = """
            attribute vec4 vPosition;
            void main() {
                gl_Position = vPosition;
            }
        """
        
        val fragmentShaderCode = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """
        
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        
        programId = GLES30.glCreateProgram()
        GLES30.glAttachShader(programId, vertexShader)
        GLES30.glAttachShader(programId, fragmentShader)
        GLES30.glLinkProgram(programId)
        
        // Create VBO for testing
        val vertices = floatArrayOf(
            0f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
        )
        
        val vbo = IntArray(1)
        GLES30.glGenBuffers(1, vbo, 0)
        vboId = vbo[0]
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.size * 4, vertices.toFloatBuffer(), GLES30.GL_STATIC_DRAW)
    }
    
    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, code)
        GLES30.glCompileShader(shader)
        return shader
    }
    
    fun destroy() {
        if (programId != -1) {
            GLES30.glDeleteProgram(programId)
            programId = -1
        }
        if (vboId != -1) {
            GLES30.glDeleteBuffers(1, intArrayOf(vboId), 0)
            vboId = -1
        }
        isInitialized = false
    }
    
    fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        GLES30.glClearColor(r, g, b, a)
    }
    
    fun clearBuffers() {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
    }
    
    fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        GLES30.glViewport(x, y, width, height)
    }
    
    fun drawTestTriangle() {
        if (!isInitialized || programId == -1 || vboId == -1) return
        
        GLES30.glUseProgram(programId)
        
        val positionHandle = GLES30.glGetAttribLocation(programId, "vPosition")
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 12, 0)
        
        val colorHandle = GLES30.glGetUniformLocation(programId, "vColor")
        GLES30.glUniform4f(colorHandle, 1f, 0f, 0f, 1f)
        
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
        
        GLES30.glDisableVertexAttribArray(positionHandle)
    }
    
    fun isInitialized(): Boolean = isInitialized
    
    fun onResume() {
        // Reinitialize if needed
    }
    
    fun onPause() {
        // Pause any ongoing operations
    }
}