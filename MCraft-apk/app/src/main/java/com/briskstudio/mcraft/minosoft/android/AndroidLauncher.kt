package de.bixilon.minosoft.android

import android.content.Intent
import de.bixilon.minosoft.gui.qmcl.LauncherActivity

object AndroidLauncher {
    fun launchQMCL() {
        val context = MinosoftApplication.instance ?: return
        val intent = Intent(context, LauncherActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}