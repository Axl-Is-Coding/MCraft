package com.briskstudio.mcraft.launcher.Gui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.briskstudio.mcraft.R

class AboutFragment: Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about_ui, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val tvVersion = view.findViewById<TextView>(R.id.tv_version)
        val tvCredits = view.findViewById<TextView>(R.id.tv_credits)
        
        tvVersion.text = "MCraft V1.0.0"
        
        tvCredits.text = buildString {
            appendLine("MCraft - Minecraft Java Edition for Android")
            appendLine()
            appendLine("Built on Minosoft by Moritz Zwerger")
            appendLine()
            appendLine("Android Port by WhoIsAxl? (Axl-Is-Coding)")
            appendLine()
            appendLine("Special thanks to:")
            appendLine("- Minosoft Contributors")
            appendLine("- Minecraft Community")
            appendLine()
            appendLine("Open Source. Community-driven. Moddable.")
            appendLine()
            appendLine("https://github.com/Axl-Is-Coding/MCraft")
        }
    }
}