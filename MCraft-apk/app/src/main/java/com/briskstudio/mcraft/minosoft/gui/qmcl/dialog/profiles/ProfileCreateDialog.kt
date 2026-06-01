package de.bixilon.minosoft.gui.qmcl.dialog.profiles

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

class ProfileCreateDialog(context: Context, onConfirm: (String) -> Unit) {
    fun show() {
        val input = EditText(context)
        AlertDialog.Builder(context)
            .setTitle("Create Profile")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString()
                if (name.isNotBlank()) onConfirm(name)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
