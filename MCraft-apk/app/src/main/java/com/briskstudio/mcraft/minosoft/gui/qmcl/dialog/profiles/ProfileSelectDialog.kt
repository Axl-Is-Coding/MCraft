package de.bixilon.minosoft.gui.qmcl.dialog.profiles

import android.app.AlertDialog
import android.content.Context

class ProfileSelectDialog(context: Context, profiles: List<String>, onSelect: (String) -> Unit) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle("Select Profile")
            .setItems(profiles.toTypedArray()) { _, which ->
                onSelect(profiles[which])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
