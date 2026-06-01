package de.bixilon.minosoft.gui.qmcl.dialog

import android.app.AlertDialog
import android.content.Context

class UpdateAvailableDialog(context: Context, version: String, onUpdate: () -> Unit) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("Version $version is available. Update now?")
            .setPositiveButton("Update") { _, _ -> onUpdate() }
            .setNegativeButton("Later", null)
            .show()
    }
}
