package com.briskstudio.mcraft.game.Render.System.opengles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils

class OpenGLESTextureManager {
    
    fun loadTexture(bitmap: Bitmap): Int {
        val textures = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        
        return textures[0]
    }
    
    fun deleteTexture(textureId: Int) {
        GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
    }
    
    fun bindTexture(textureId: Int, unit: Int) {
        GLES30.glActiveUnit(unit)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
    }
}