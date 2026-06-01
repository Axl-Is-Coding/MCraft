package de.bixilon.minosoft.gui.qmcl.dialog.simple

import android.app.AlertDialog
import android.content.Context

class InfoDialog(context: Context, title: String, message: String) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
