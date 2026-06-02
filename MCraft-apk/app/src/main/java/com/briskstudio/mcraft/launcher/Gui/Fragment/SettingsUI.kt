package com.briskstudio.mcraft.launcher.Gui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.briskstudio.mcraft.R
import com.briskstudio.mcraft.launcher.Backend.Launcher
import com.briskstudio.mcraft.UserData.Data.SettingPrefs

class SettingsUI : Fragment() {
    
    private lateinit var seekRenderDistance: SeekBar
    private lateinit var tvRenderDistance: TextView
    private lateinit var switchMusic: Switch
    private lateinit var switchSound: Switch
    private lateinit var switchShowFps: Switch
    private lateinit var switchTouchControls: Switch
    private lateinit var btnSave: Button
    
    private var currentSettings: SettingPrefs? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_ui, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        seekRenderDistance = view.findViewById(R.id.seek_render_distance)
        tvRenderDistance = view.findViewById(R.id.tv_render_distance)
        switchMusic = view.findViewById(R.id.switch_music)
        switchSound = view.findViewById(R.id.switch_sound)
        switchShowFps = view.findViewById(R.id.switch_show_fps)
        switchTouchControls = view.findViewById(R.id.switch_touch_controls)
        btnSave = view.findViewById(R.id.btn_save)
        
        loadSettings()
        
        seekRenderDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvRenderDistance.text = "Render Distance: ${progress + 4} chunks"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        btnSave.setOnClickListener {
            saveSettings()
        }
    }
    
    private fun loadSettings() {
        currentSettings = Launcher.getSettings()
        currentSettings?.let { settings ->
            seekRenderDistance.progress = settings.renderDistance - 4
            tvRenderDistance.text = "Render Distance: ${settings.renderDistance} chunks"
            switchMusic.isChecked = settings.musicVolume > 0
            switchSound.isChecked = settings.soundVolume > 0
            switchShowFps.isChecked = settings.showFps
            switchTouchControls.isChecked = settings.touchControls
        }
    }
    
    private fun saveSettings() {
        currentSettings?.let { settings ->
            settings.renderDistance = seekRenderDistance.progress + 4
            settings.musicVolume = if (switchMusic.isChecked) 50 else 0
            settings.soundVolume = if (switchSound.isChecked) 50 else 0
            settings.showFps = switchShowFps.isChecked
            settings.touchControls = switchTouchControls.isChecked
            
            Launcher.saveSettings(settings)
            
            android.widget.Toast.makeText(context, "Settings saved", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}