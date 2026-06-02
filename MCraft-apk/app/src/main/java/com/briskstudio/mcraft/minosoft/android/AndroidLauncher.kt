package de.bixilon.minosoft.android

import android.content.Intent
// Change from .gui.qmcl.LauncherActivity to .launcher.LauncherActivity
import com.briskstudio.mcraft.launcher.LauncherActivity

object AndroidLauncher {
    fun launchQMCL() {
        val context = MinosoftApplication.instance ?: return
        val intent = Intent(context, LauncherActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}