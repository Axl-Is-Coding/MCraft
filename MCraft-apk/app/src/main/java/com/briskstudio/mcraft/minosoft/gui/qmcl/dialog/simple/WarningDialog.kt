package de.bixilon.minosoft.gui.qmcl.dialog.simple

import android.app.AlertDialog
import android.content.Context

class WarningDialog(context: Context, title: String, message: String) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}
