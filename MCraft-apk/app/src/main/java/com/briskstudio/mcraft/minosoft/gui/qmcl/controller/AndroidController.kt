package de.bixilon.minosoft.gui.qmcl.controller

import androidx.appcompat.app.AppCompatActivity

abstract class AndroidController : AppCompatActivity() {
    abstract fun initialize()
    abstract fun onBackPressedCustom(): Boolean
}
