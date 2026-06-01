package de.bixilon.minosoft.gui.qmcl.main

import android.widget.TextView

class InfoPane(private val textView: TextView) {
    fun setInfo(text: String) {
        textView.text = text
    }
    
    fun clear() {
        textView.text = ""
    }
}
