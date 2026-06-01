package de.bixilon.minosoft.gui.qmcl.util

import android.content.Context
import android.widget.Toast

object QmclUtil {
    
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    fun formatVersionName(id: String): String {
        return "Minecraft $id"
    }
}
