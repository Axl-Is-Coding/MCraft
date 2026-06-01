package de.bixilon.minosoft.gui.qmcl.dialog

import android.app.ProgressDialog
import android.content.Context

class PleaseWaitDialog(context: Context) {
    private val dialog = ProgressDialog(context).apply {
        setMessage("Please wait...")
        setCancelable(false)
    }
    
    fun show() = dialog.show()
    fun dismiss() = dialog.dismiss()
}
