package de.bixilon.minosoft.gui.qmcl.util.cell

import android.view.View
import android.widget.TextView

class VersionListCell(itemView: View) {
    val versionName: TextView = itemView.findViewById(android.R.id.text1)
    val versionStatus: TextView = itemView.findViewById(android.R.id.text2)
}
