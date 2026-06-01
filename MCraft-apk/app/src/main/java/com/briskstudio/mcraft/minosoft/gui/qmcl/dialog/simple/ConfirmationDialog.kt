package de.bixilon.minosoft.gui.qmcl.dialog.simple

import android.app.AlertDialog
import android.content.Context

class ConfirmationDialog(
    context: Context,
    title: String,
    message: String,
    onConfirm: () -> Unit
) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
